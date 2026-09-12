#!/usr/bin/env bash

export NAME_PREFIX="petcare"
export LOCATION="canadacentral"
export RESOURCE_GROUP="rg-${NAME_PREFIX}"
export ACR_NAME="acr${NAME_PREFIX}"
export STORAGE_ACCOUNT="str${NAME_PREFIX}"
export FILE_SHARE_NAME="oracle-data"

export ORACLE_DB_URL="jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"
export ORACLE_USER="rm566548"
export ORACLE_PASSWORD_USER="130506"

export DB_ACI_NAME="${NAME_PREFIX}-aci-db"
export API_ACI_NAME="${NAME_PREFIX}-aci-api"
export DB_IMAGE_NAME="${NAME_PREFIX}-db:v1"
export API_IMAGE_NAME="${NAME_PREFIX}-api:v1"