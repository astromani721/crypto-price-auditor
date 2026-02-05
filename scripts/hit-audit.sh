#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
SYMBOLS=(BTC ETH SOL ADA XRP DOGE LTC BNB AVAX MATIC)
SLEEP_SECONDS="${SLEEP_SECONDS:-1}"

while true; do
  SYMBOL="${SYMBOLS[$RANDOM % ${#SYMBOLS[@]}]}"
  echo "POST ${BASE_URL}/api/audit/${SYMBOL}"
  curl -sS -X POST -w "\nHTTP_STATUS:%{http_code}\n" "${BASE_URL}/api/audit/${SYMBOL}"
  echo ""
  sleep "${SLEEP_SECONDS}"
done
