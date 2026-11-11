# URL Shortener

## Day 1 scope (you build this)

**API**
- `POST /api/urls` with `{"url": "https://example.com/very/long"}`
  → `201 Created`, body `{"code": "...", "shortUrl": "http://localhost:8081/..."}`
- `GET /{code}` → redirect to the original URL with a `Location` header
- Unknown code → `404`; invalid URL (not http/https, blank, too long) → `400`

**Decisions to make (be ready to defend each in an interview)**
1. **How codes are generated.** Options:
   - Base62-encode a database sequence ID: short, never collides, no retry loop,
     but codes are sequential and guessable.
   - Random 7-char code + unique constraint, retry on collision: not guessable,
     but needs a retry path.
   - Hash of the URL (truncated): the same URL always gets the same code, but
     truncation creates collisions you still have to handle.
2. **302 vs 301.** A 301 is cached by browsers, which takes load off your service
   but means you never see repeat clicks. A 302 sends every click back through you.
3. **The same URL submitted twice:** new code or reuse the existing one?
4. **Table design:** columns, types, which index makes `GET /{code}` fast.

**Write first**
- `V1__create_urls.sql` in `src/main/resources/db/migration/`
- `Base62` codec (pure Java, no Spring) and its unit tests:
  `0 → "0"`, `61 → "z"` (or whatever your alphabet gives), `62 → "10"`,
  round-trip for random longs including `Long.MAX_VALUE`, and rejecting bad input
  (negative numbers, characters outside the alphabet)
- Repository (`JdbcClient`), service, controller
- An integration test: create → redirect → 404, against real Postgres
  (reuse `TestcontainersConfiguration`)

**Done when**
```bash
curl -i -X POST localhost:8081/api/urls -H "Content-Type: application/json" -d "{\"url\":\"https://example.com\"}"
curl -i localhost:8081/<code>      # 302 + Location: https://example.com
curl -i localhost:8081/nope        # 404
```
…and `mvn verify` is green locally and in CI.

**Numbers to remember:** 62^7 ≈ 3.5 trillion codes from 7 characters.

## Day 2 (preview)
Redis cache-aside on `GET /{code}`, then k6: p99 with cache vs. without.

## Results
_Day 10_
