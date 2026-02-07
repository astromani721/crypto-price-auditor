# Contributing

## Prerequisites

- Java 21 (21.x)
- Maven 3.9.x (3.9 - <4.0)
- Docker + Docker Compose (for containers/observability)

## Build and verify

```bash
mvn clean verify
```

This runs:
- OWASP Dependency-Check (requires NVD API key in `~/.m2/settings.xml`)
- PMD (priority 1-2)
- SpotBugs
- Checkstyle (Google rules)
- JaCoCo coverage gates (line >= 60%, branch >= 50%)

### NVD API key setup

Add this to `~/.m2/settings.xml`:

```xml
<servers>
  <server>
    <id>nvd</id>
    <password>YOUR_NVD_API_KEY</password>
  </server>
</servers>
```

## Run locally

```bash
mvn spring-boot:run
```

## Docker Compose

```bash
docker compose up --build
```

## Observability

- Actuator health: `http://localhost:8080/actuator/health`
- Prometheus metrics: `http://localhost:8080/actuator/prometheus`
- Prometheus UI: `http://localhost:9090`
- Grafana UI: `http://localhost:3000` (default login: admin / admin)
- Grafana data source URL (container): `http://prometheus:9090`

### Custom metrics

- `audit.price.count{symbol="BTC"}`: request count for `POST /api/audit/{symbol}`
- `audit.price.latency{symbol="BTC"}`: request latency for `POST /api/audit/{symbol}`
- `coinbase.spot.count{symbol="BTC"}`: Coinbase spot request count
- `coinbase.spot.latency{symbol="BTC"}`: Coinbase spot request latency

## Scripts

Load generator:

```bash
./scripts/hit-audit.sh
```

Optional:
```bash
BASE_URL=http://localhost:8080 SLEEP_SECONDS=1 ./scripts/hit-audit.sh
```
