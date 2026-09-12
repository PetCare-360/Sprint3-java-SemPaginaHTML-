#!/usr/bin/env bash
set -e

source ./01-variaveis.sh

echo "Criando Resource Group..."
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION"

echo "Criando Azure Container Registry..."
az acr create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$ACR_NAME" \
  --sku Basic \
  --admin-enabled true

echo "Realizando login no ACR..."
az acr login \
  --name "$ACR_NAME"

echo "ACR criado com sucesso."