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

echo "Criando ACI da API Java..."

az container create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$API_ACI_NAME" \
  --image "$ACR_LOGIN_SERVER/$API_IMAGE_NAME" \
  --registry-login-server "$ACR_LOGIN_SERVER" \
  --registry-username "$ACR_USERNAME" \
  --registry-password "$ACR_PASSWORD" \
  --dns-name-label "$API_ACI_NAME" \
  --os-type Linux \
  --cpu 2 \
  --memory 4 \
  --ports 8080 \
  --restart-policy Always \
  --environment-variables \
    ORACLE_DB_URL="$ORACLE_DB_URL" \
  --secure-environment-variables \
    ORACLE_DB_USERNAME="$ORACLE_USER" \
    ORACLE_DB_PASSWORD="$ORACLE_PASSWORD_USER"

echo "ACI Java criado."