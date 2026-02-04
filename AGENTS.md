# AGENTS.md

## Context
You are an expert Senior Java Architect. Your task is to scaffold a "Containerization Learning POC" project. This project is a Spring Boot application that acts as a "Generic Price Auditor." It fetches the current price of *any* cryptocurrency (BTC, ETH, SOL, etc.) from the Coinbase API and stores it in a PostgreSQL database for historical auditing.

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
