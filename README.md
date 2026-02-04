# Crypto Price Auditor (Containerization POC)

A Spring Boot app that fetches the current spot price of any cryptocurrency from the Coinbase API and stores it in PostgreSQL for historical auditing.

## Tech Stack
* **App:** Java 21, Spring Boot 3.x
* **Database:** PostgreSQL 15
* **Tooling:** pgAdmin 4
* **Infrastructure:** Docker & Docker Compose

## Prerequisites
* Docker Desktop installed and running.

## Run locally

```bash
mvn spring-boot:run
```

## Run with Docker

```bash
docker-compose up --build
```

## Using the API
### A. Audit a Price (Ingest) Trigger a fetch for different assets. This calls Coinbase and saves the result to the DB.

#### Audit Bitcoin
```bash
curl -X POST http://localhost:8080/api/audit/BTC
```
#### Audit Ethereum
```bash
curl -X POST http://localhost:8080/api/audit/ETH
```
#### Audit Solana
```bash
curl -X POST http://localhost:8080/api/audit/SOL
```


### B. View History (Serve) View the audit trail.
#### View All History
```bash
curl http://localhost:8080/api/audit/history
```

#### View Only Ethereum History
```bash
curl http://localhost:8080/api/audit/history/ETH
```

## Manager Database (pgAdmin)

1. Go to http://localhost:5050

2. Login: admin@admin.com / admin

3. Add Server:
   + Server Name: local-pg
   + Host name/address: db 
   + Port: 5432
   + Database: auditdb
   + User: myuser
   + Pass: mypassword

## Notes

- Database defaults (override via env vars):
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auditdb`
  - `SPRING_DATASOURCE_USERNAME=myuser`
  - `SPRING_DATASOURCE_PASSWORD=mypassword`
