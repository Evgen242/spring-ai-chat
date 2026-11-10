# Runbook — How to Reproduce the Test Run

**Purpose:** Step-by-step instructions to reproduce the Level 3 test run (Spring AI Chat RAG + API/HTTP).
**Audience:** Curator, QA engineer, or any reviewer.
**Time:** ~7 minutes (newman run) + 1 minute (health check).

---

## Prerequisites

| Tool | Version | Purpose |
|---|---|---|
| Docker | 20+ | Run newman (Postman CLI) in container |
| curl | any | Quick health check |
| jq | any | Pretty-print JSON (optional) |

No local Postman installation is required — the collection runs via the `postman/newman` Docker image.

---

## Option 1 — Run against deployed VPS (fastest, 7 min)

The service is deployed at `http://194.154.27.141:8082`. If it is up, run:

```bash
# 1. Check health
curl -s -w "\nHTTP %{http_code}, %{time_total}s\n" http://194.154.27.141:8082/api/health
# Expected: OK, HTTP 200, <50ms

# 2. Run newman (all 15 cases A1-A15)
cd ~/spring-ai-chat

docker run --rm \
  -v $(pwd)/qa/postman:/etc/newman \
  postman/newman run spring-ai-chat.postman_collection.json \
  -e env.json \
  --timeout-request 300000 \
  --reporters cli \
  2>&1 | tail -50
```

**Expected result:**

```text
requests:    15  | failed: 1   (A14 → ESOCKETTIMEDOUT, expected → BUG-007)
assertions:  30  | failed: 2   (both on A14)
duration:    ~7 min
```

**What is A14?** POST `/api/rag` with 5000 characters. The free-tier model (`nvidia/nemotron-3.5-lightning:free`) does not respond within 300 s, so newman reports `errored`. **This is expected** and documented as **BUG-007** (Low/Low, edge case). Do not treat it as a test failure.

---

## Option 2 — Run locally (own VPS / laptop)

### 2.1. Clone and configure

```bash
git clone https://github.com/Evgen242/spring-ai-chat.git
cd spring-ai-chat

# Set OpenRouter API key (free tier: 50 req/day)
export OPENROUTER_API_KEY="sk-or-..."
```

### 2.2. Build and start

```bash
docker compose up -d --build
```

Wait **30–60 seconds** for Spring Boot to start and index the dataset (468 chunks from `src/main/resources/sample.txt`, 650 KB).

### 2.3. Verify health

```bash
curl -s http://localhost:8080/api/health
# Expected: OK
```

### 2.4. Run newman against localhost

Update `qa/postman/env.json` (or override with `--env-var`):

```bash
cd ~/spring-ai-chat

docker run --rm \
  -v $(pwd)/qa/postman:/etc/newman \
  postman/newman run spring-ai-chat.postman_collection.json \
  -e env.json \
  --env-var "baseUrl=http://host.docker.internal:8080" \
  --timeout-request 300000 \
  --reporters cli \
  2>&1 | tail -50
```

> On Linux, `host.docker.internal` may not resolve. Use `--network host` and `http://localhost:8080` instead.

**Expected result:** same as Option 1 — 14/15 PASS, A14 timeout.

---

## Option 3 — Import into Postman UI (manual, for exploration)

1. Open Postman → **File → Import** → select `qa/postman/spring-ai-chat.postman_collection.json`.
2. Import environment: **File → Import** → `qa/postman/env.json`.
3. Select the environment **"Spring AI Chat — VPS"** (top-right dropdown).
4. If needed, edit `baseUrl` in the environment (e.g. `http://localhost:8080`).
5. Right-click the collection → **Run collection**.
6. Set **Delay** = 1000 ms, **Timeout** = 300000 ms.
7. Click **Run Spring AI Chat — RAG API (A1-A15)**.

**Expected:** 14/15 requests PASS (A14 → timeout).

---

## What to compare after the run

| Source | Expected |
|---|---|
| newman CLI output | `requests: 15, failed: 1`; `assertions: 30, failed: 2` |
| `qa/postman/newman-summary.json` | `requests {total:15, failed:1}`, `assertions {total:30, failed:2}` |
| `qa/api-tests.md` (table A1–A15) | 14 PASS, 1 FAIL (A14 → BUG-007) |
| `qa/summary.md` → Results | 46/47 = 97.9% |

All three sources must agree. If they do not — that is a **new defect** to report.

---

## Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| **A14 `errored` (ESOCKETIMEDOUT)** | Expected — free model timeout on 5000-char prompt | Not a bug — see `qa/bug-reports.md` → BUG-007 |
| **429 Too Many Requests** | OpenRouter free-tier limit: 50 req/day | Wait 24 h or use a paid key |
| **400 Bad Request** on Cyrillic (Git Bash) | Terminal encoding (MINGW64) | Use `jq -Rs '{message: .}'` or Postman UI |
| **Connection refused** | VPS down / wrong port | `docker ps`; check port `8082` (VPS) or `8080` (local) |
| **Slow responses (20–45 s)** | Free-tier model latency | Expected — see BUG-006 |
| **`sources` non-empty on refusal** | Regression of BUG-003 | Check `RAGService.java` — `finalSources = List.of()` on refusal |
| **468 chunks not indexed** | Dataset missing / wrong path | Check `src/main/resources/sample.txt`, logs on startup |

---

## Related artifacts

| Artifact | Path |
|---|---|
| Test Plan | `qa/test-plan.md` |
| Test Design (4 techniques) | `qa/test-design.md` |
| API test cases + results | `qa/api-tests.md` |
| Bug reports (BUG-001…007) | `qa/bug-reports.md` |
| Test Summary Report | `qa/summary.md` |
| Postman collection | `qa/postman/spring-ai-chat.postman_collection.json` |
| Newman summary | `qa/postman/newman-summary.json` |
