#!/bin/bash
set -Eeuo pipefail
umask 077

APP_DIR="/opt/curso-api"
ENV_FILE="$${APP_DIR}/.env.prod"
TEMP_ENV_FILE="$${APP_DIR}/.env.prod.tmp"
AWS_REGION="${aws_region}"
COMPOSE_VERSION="v5.7.0"
COMPOSE_PLUGIN_DIR="/usr/libexec/docker/cli-plugins"

DB_URL_PARAMETER="${db_url_parameter}"
DB_USERNAME_PARAMETER="${db_username_parameter}"
DB_PASSWORD_PARAMETER="${db_password_parameter}"
JWT_SECRET_PARAMETER="${jwt_secret_parameter}"
JWT_EXPIRATION_PARAMETER="${jwt_expiration_parameter}"

log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

on_error() {
  log "ERROR: Falló el bootstrap en la línea $${1}."
  exit 1
}

trap 'on_error $${LINENO}' ERR

log "Actualizando paquetes..."
dnf update -y

log "Instalando Docker, Git y AWS CLI..."
dnf install -y docker git awscli-2

log "Configurando Docker..."
systemctl enable --now docker
usermod -aG docker ec2-user

log "Instalando Docker Compose..."
command -v curl >/dev/null 2>&1
install -d -m 755 "$${COMPOSE_PLUGIN_DIR}"
curl -fL \
  "https://github.com/docker/compose/releases/download/$${COMPOSE_VERSION}/docker-compose-linux-$$(uname -m)" \
  -o "$${COMPOSE_PLUGIN_DIR}/docker-compose"
chmod +x "$${COMPOSE_PLUGIN_DIR}/docker-compose"
docker compose version

log "Creando directorio de la aplicación..."
install -d -o ec2-user -g ec2-user -m 750 "$${APP_DIR}"

get_ssm_parameter() {
  local parameter_name="$1"

  aws ssm get-parameter \
    --name "$${parameter_name}" \
    --with-decryption \
    --query "Parameter.Value" \
    --output text \
    --region "$${AWS_REGION}"
}

log "Obteniendo parámetros desde SSM..."

DB_URL="$$(get_ssm_parameter "$${DB_URL_PARAMETER}")"
DB_USERNAME="$$(get_ssm_parameter "$${DB_USERNAME_PARAMETER}")"
DB_PASSWORD="$$(get_ssm_parameter "$${DB_PASSWORD_PARAMETER}")"
JWT_SECRET="$$(get_ssm_parameter "$${JWT_SECRET_PARAMETER}")"
JWT_EXPIRATION_MS="$$(get_ssm_parameter "$${JWT_EXPIRATION_PARAMETER}")"

log "Generando archivo de entorno..."

cat > "$${TEMP_ENV_FILE}" <<EOF
DB_URL="$${DB_URL}"
DB_USERNAME="$${DB_USERNAME}"
DB_PASSWORD="$${DB_PASSWORD}"
JWT_SECRET="$${JWT_SECRET}"
JWT_EXPIRATION_MS="$${JWT_EXPIRATION_MS}"
EOF

test -s "$${TEMP_ENV_FILE}"
chown ec2-user:ec2-user "$${TEMP_ENV_FILE}"
chmod 600 "$${TEMP_ENV_FILE}"
mv "$${TEMP_ENV_FILE}" "$${ENV_FILE}"

unset DB_URL DB_USERNAME DB_PASSWORD JWT_SECRET JWT_EXPIRATION_MS
rm -f "$${TEMP_ENV_FILE}"

log "Bootstrap completado."
echo "Docker y Docker Compose instalados; variables SSM configuradas" \
  > /home/ec2-user/bootstrap-status.txt
chown ec2-user:ec2-user /home/ec2-user/bootstrap-status.txt
chmod 644 /home/ec2-user/bootstrap-status.txt
