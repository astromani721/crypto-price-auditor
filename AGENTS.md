# AGENTS.md

## Context
You are an expert Senior Java Architect. Your task is to scaffold a "Containerization Learning POC" project. This project is a Spring Boot application that acts as a "Generic Price Auditor." It fetches the current price of *any* cryptocurrency (BTC, ETH, SOL, etc.) from the Coinbase API and stores it in a PostgreSQL database for historical auditing.

## Requirements
- Java 21, Spring Boot 3.x, Maven
- PostgreSQL 15, Redis 7, pgAdmin 4
- Dockerfile builds the app and runs tests (no `-DskipTests`)
- Caching: Redis-backed with TTL default 10s (override via `CRYPTO_CACHE_TTL`)
- Write endpoint saves audit rows; read-only endpoint does not

### Endpoints
- `POST /api/audit/{symbol}`: fetch price and save to DB (audit)
- `GET /api/audit/{symbol}/spot`: fetch price only (no DB write)
- `GET /api/audit/history`: audit history (descending by id)
- `GET /api/audit/history/{symbol}`: symbol history (descending by id)

### Configuration
- DB: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- JPA: `SPRING_JPA_HIBERNATE_DDL_AUTO`
- Redis: `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`
- Cache TTL: `CRYPTO_CACHE_TTL` (default `10s`)

## Directory Structure
Ensure the project follows this standard Maven layout:

```text
.
├── pom.xml
├── README.md
├── Dockerfile
├── docker-compose.yml
└── src
    └── main
        ├── java
        │   └── com
        │       └── example
        │           └── dockerpoc
        │               ├── DockerPocApplication.java
        │               ├── CacheConfig.java
        │               ├── PriceEntity.java
        │               ├── CoinbaseResponse.java
        │               ├── PriceRepository.java
        │               ├── CryptoService.java
        │               └── AuditController.java
        └── resources
            └── application.properties
