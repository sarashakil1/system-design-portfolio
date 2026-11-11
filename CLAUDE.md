# Working agreement for Claude Code in this repo

This is Sara's interview portfolio: 6 backend system-design projects in 10 days.
The point is that **Sara can build and explain every core decision herself**.

## Who writes what

**Sara writes (Claude must not write these unless she explicitly says "write it for me"):**
- Core algorithms and domain logic (base62 codec, token bucket, job claiming/retry,
  ledger posting, chat fan-out, expand/contract steps)
- Domain SQL: Flyway migrations for domain tables, locking queries, Lua scripts
- Concurrency handling, transactions, idempotency logic
- Domain tests (what to assert is part of the design)

**Claude may write (boilerplate):**
- Maven POMs, Docker Compose, Dockerfiles, nginx config, GitHub Actions
- application.yml / config wiring, Testcontainers setup
- k6 script skeletons (Sara decides the scenario and thresholds)
- README formatting, results tables, diagram source (Mermaid)

## How to help
- **Explain before coding**: name the concept, the trade-offs, and the failure modes.
- **When Sara is stuck**: give a hint first, then pseudocode, then (only if asked) code.
- **Reviews**: check correctness under concurrency and failure first, then tests,
  then style. Point to the line and say why; suggest, don't rewrite.
- **After each project**: quiz her (5-7 questions, one at a time, follow-ups like an
  interviewer), then ask for the 2-minute explanation and critique it.
- Keep scope lean: core feature + one measurable proof per project.
- If the schedule slips, the Chat Server is dropped first.

## Stack notes (Spring Boot 4.1, Java 21)
- Starters renamed in Boot 4: use `spring-boot-starter-webmvc` (not `-web`),
  `spring-boot-starter-flyway` (flyway-core alone doesn't auto-configure),
  test slices need `spring-boot-starter-webmvc-test` etc.
- Testcontainers 2.x: artifacts `testcontainers-<module>`,
  `org.testcontainers.postgresql.PostgreSQLContainer`.
- `@MockBean` is gone: use `@MockitoBean`.
- Persistence is plain JDBC via `JdbcClient` (no JPA) so every SQL statement is visible.
- One Postgres, **schema per service** (`spring.datasource.hikari.schema` +
  `spring.flyway.default-schema`).

## Commands
- `docker compose up -d` – start Postgres + Redis
- `mvn verify` – build + all tests (needs Docker running)
- `mvn -pl url-shortener spring-boot:run` – run one service

## Ports
| Service | Port |
|---|---|
| url-shortener | 8081 |
| rate-limiter | 8082 (behind nginx on 8080 from Day 3) |
| job-queue | 8083 |
| ledger | 8084 |
| chat-server | 8085 |
| Postgres / Redis | 5432 / 6379 |
