#!/usr/bin/env bash
set -e

source ./01-variaveis.sh

ACR_LOGIN_SERVER=$(az acr show \
  --name "$ACR_NAME" \
  --query loginServer \
  --output tsv)

ACR_USERNAME=$(az acr credential show \
  --name "$ACR_NAME" \
  --query username \
  --output tsv)

ACR_PASSWORD=$(az acr credential show \
  --name "$ACR_NAME" \
  --query "passwords[0].value" \
  --output tsv)

echo "Criando ACI responsável pela configuração do banco..."

az container create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$DB_ACI_NAME" \
  --image "${ACR_LOGIN_SERVER}/${DB_IMAGE_NAME}" \
  --registry-login-server "$ACR_LOGIN_SERVER" \
  --registry-username "$ACR_USERNAME" \
  --registry-password "$ACR_PASSWORD" \
  --os-type Linux \
  --cpu 3 \
  --memory 6 \
  --restart-policy Never \
  --environment-variables \
    ORACLE_DB_URL="$ORACLE_DB_URL" \
  --secure-environment-variables \
    ORACLE_DB_USERNAME="$ORACLE_USER" \
    ORACLE_DB_PASSWORD="$ORACLE_PASSWORD_USER"