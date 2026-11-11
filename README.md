# System Design Portfolio

Six backend system-design projects, each with a core feature and **one measurable proof**.
Java 21 · Spring Boot 4.1 · PostgreSQL 17 · Redis 7 · nginx · Docker Compose · Flyway ·
Testcontainers · GitHub Actions · k6

![CI](../../actions/workflows/ci.yml/badge.svg)

| # | Project | Core idea | Proof | Status |
|---|---|---|---|---|
| 1 | [URL Shortener](url-shortener/) | Base62 codes, 302 redirect, Redis cache | k6 p99 with vs. without cache | 🚧 |
| 2 | Rate Limiter | Redis + Lua token bucket, 429 + headers | Limit holds across 3 instances behind nginx | ⏳ |
| 3 | Job Queue | Postgres `SKIP LOCKED`, backoff retries, dead-letter | Kill a worker mid-job, job still completes | ⏳ |
| 4 | Payments Ledger | Double-entry, cents, idempotency keys, ordered locking | 1,000 concurrent transfers, total unchanged | ⏳ |
| 5 | Chat Server | WebSocket rooms, Postgres history, Redis pub/sub | Messages cross 2 instances | ⏳ |
| 6 | Zero-downtime Migration | Expand/contract column rename on 1M rows | Zero errors under load | ⏳ |

## Results

| Project | Scenario | req/s | p99 | Error rate |
|---|---|---|---|---|
| _filled in on Day 10_ | | | | |

## Run it

Requirements: JDK 21, Maven 3.9+, Docker Desktop.

```bash
docker compose up -d                       # Postgres + Redis
mvn verify                                 # build + tests (Testcontainers needs Docker)
mvn -pl url-shortener spring-boot:run      # http://localhost:8081/actuator/health
```

## Layout

```
.
├── docker-compose.yml        shared Postgres + Redis (one schema per service)
├── pom.xml                   parent POM, one Maven module per project
├── url-shortener/            project 1
├── load-tests/               k6 scripts
├── docs/                     architecture diagrams
└── .github/workflows/ci.yml  build + Testcontainers tests on every push
```
