# QA — Manual & API Testing (Level 3, Spring AI Chat RAG)

## Quick links

| Artifact | Path |
|---|---|
| API/HTTP tests (Postman A1–A15) | `api-tests.md` |
| Bug reports (BUG-001…007) | `bug-reports.md` |
| Test summary report | `summary.md` |
| Postman collection + env + summary | `postman/` |

## Run Postman collection (A1–A15) via Docker

```bash
docker run --rm \
  -v $(pwd)/qa/postman:/etc/newman \
  postman/newman run spring-ai-chat.postman_collection.json \
  -e env.json \
  --timeout-request 300000 \
  --reporters cli,json \
  --reporter-json-export /etc/newman/newman-report.json
Artifacts:

postman/newman-summary.json — summarized run report (committed).

postman/newman-report.json — full log (large, git-ignored).

Result (final)
Suite	PASS / Total	%
RAG (manual)	20 / 20	100%
JUnit (automated)	12 / 12	100%
API/HTTP (Postman)	14 / 15	93.3%
Overall	46 / 47	97.9%
See summary.md and BUG-007 (A14 timeout, Low/Low, edge case).
