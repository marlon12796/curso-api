#!/usr/bin/env bash

# Hace el despliegue de una versión publicada en una EC2 mediante AWS SSM.
#
# El runner de GitHub no entra por SSH ni necesita una llave .pem.
# SSM ejecuta los comandos directamente dentro de la instancia.

set -Eeuo pipefail

: "${AWS_REGION:?AWS_REGION es obligatorio}"
: "${EC2_INSTANCE_ID:?EC2_INSTANCE_ID es obligatorio}"
: "${ECR_REPOSITORY_URL:?ECR_REPOSITORY_URL es obligatorio}"
: "${IMAGE_TAG:?IMAGE_TAG es obligatorio}"

echo "Preparando despliegue de ${IMAGE_TAG} en ${EC2_INSTANCE_ID}..."

# jq convierte los comandos en el formato JSON que requiere send-command.
# Estos comandos se ejecutarán dentro de EC2, no en el runner de GitHub.
COMMANDS=$(jq -n \
  --arg region "$AWS_REGION" \
  --arg repository "$ECR_REPOSITORY_URL" \
  --arg tag "$IMAGE_TAG" \
  '{commands: [
    # Si un comando falla, SSM marcará el despliegue como fallido.
    "set -e",

    # La aplicación y su docker-compose viven en este directorio.
    "cd /opt/curso-api",

    # La instancia usa su IAM Role para autenticarse contra ECR.
    ("aws ecr get-login-password --region " + $region + " | docker login --username AWS --password-stdin " + $repository),

    # Descarga solamente la imagen versionada de la API.
    ("IMAGE_TAG=" + $tag + " ECR_REPOSITORY_URL=" + $repository + " docker compose --profile production --env-file /opt/curso-api/.env.prod -f /opt/curso-api/docker-compose.yml pull api"),

    # Reemplaza solo api. PostgreSQL y Cloudflare Tunnel permanecen activos.
    ("IMAGE_TAG=" + $tag + " ECR_REPOSITORY_URL=" + $repository + " docker compose --profile production --env-file /opt/curso-api/.env.prod -f /opt/curso-api/docker-compose.yml up -d --no-build --no-deps api"),

    # Limpia imágenes antiguas que tengan más de siete días.
    "docker image prune -af --filter until=168h"
  ]}')

# Envía el script remoto a la instancia mediante Systems Manager.
COMMAND_ID=$(aws ssm send-command \
  --instance-ids "$EC2_INSTANCE_ID" \
  --document-name "AWS-RunShellScript" \
  --comment "Deploy release $IMAGE_TAG" \
  --parameters "$COMMANDS" \
  --query "Command.CommandId" \
  --output text)

echo "Comando SSM enviado: ${COMMAND_ID}"

# Espera como máximo seis minutos: 36 intentos de diez segundos.
for attempt in $(seq 1 36); do
  STATUS=$(aws ssm get-command-invocation \
    --command-id "$COMMAND_ID" \
    --instance-id "$EC2_INSTANCE_ID" \
    --query "Status" \
    --output text)

  case "$STATUS" in
    Success)
      echo "Despliegue completado: ${IMAGE_TAG}"
      exit 0
      ;;
    Failed|Cancelled|TimedOut|Cancelling)
      echo "El despliegue falló con estado: ${STATUS}"
      aws ssm get-command-invocation \
        --command-id "$COMMAND_ID" \
        --instance-id "$EC2_INSTANCE_ID" \
        --query '{status:Status,stdout:StandardOutputContent,stderr:StandardErrorContent}'
      exit 1
      ;;
  esac

  sleep 10
done

echo "Tiempo agotado esperando el comando SSM: ${COMMAND_ID}"
exit 1
