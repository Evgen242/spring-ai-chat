
# Test Plan — Spring AI Chat RAG (Level 3)

**Version:** 1.0
**Date:** 2026-11-09
**Author:** QA Engineer (Deploy26)
**Object under test:** Spring AI Chat Application — RAG + Function Calling, VPS `194.154.27.141:8082`
**Companion documents:** `qa/api-tests.md`, `qa/test-design.md`, `qa/bug-reports.md`, `qa/summary.md`

## 1. Introduction

This test plan describes the manual and API/HTTP testing of the Spring AI Chat Application — a RAG-based (Retrieval-Augmented Generation) service with Function Calling, deployed in Docker on a VPS. The plan covers scope, strategy, resources, risks, entry/exit criteria, and artifacts.

## 2. Objectives

1. Verify RAG retrieval accuracy: aggregation (SITE, DEP, role), exact IDs (EMP), names with morphology (cases), markers (10-char), dates.
2. Verify Function Calling: `getCurrentTime`, `calculate`, `getSystemInfo`.
3. Verify boundary handling: non-existent IDs/sites/employees → correct refusal (`sources: []`).
4. Verify HTTP API contract: status codes, JSON structure, validation errors (400/415/405), `sources` behavior.
5. Log defects with correct severity/priority and produce a Test Summary Report.

## 3. Scope

### In scope

- RAG functional testing (manual): 20 cases across aggregation, exact data, boundaries.
- API/HTTP testing (Postman + newman): 15 cases A1–A15.
- JUnit automated tests: 12 cases (cross-check).
- Boundary and negative testing (empty, null, invalid JSON, wrong Content-Type, 5000-char input).
- Bug reporting and Test Summary Report.

### Out of scope

- Load / stress testing (documented as BUG-001 — 23 MB does not fit 2 GB RAM VPS).
- Security / authentication testing (no auth layer in current scope).
- UI testing (no UI).
- Full 23 MB dataset (infra limit — see BUG-001).
- Cross-browser testing (API only).

## 4. Test Strategy

| Layer | Approach | Tools |
|---|---|---|
| RAG functional | Manual, 4 test-design techniques | curl, jq |
| API/HTTP | Automated collection, assertions | Postman Collection v2.1, newman in Docker |
| JUnit | Automated unit tests | Maven 3.9.6, JUnit 5 |
| Bug tracking | Table format with severity/priority | Markdown (`qa/bug-reports.md`) |
| Reporting | Test Summary Report | Markdown (`qa/summary.md`) |

**Test-design techniques applied:** equivalence classes (EC), boundary values (BV), decision table (DT), state transition (ST). Details — in `qa/test-design.md`.

## 5. Resources

| Resource | Details |
|---|---|
| QA engineer | 1 (manual + API) |
| Environment | VPS Ubuntu, Docker 29.6.2, container `Up (healthy)` |
| Tools | curl, jq, Docker, Postman/newman, Maven, JUnit 5 |
| Test data | Dataset "Nevarys", 650 KB, 468 chunks |
| Model | `nvidia/nemotron-3.5-lightning:free` via OpenRouter |
| Time budget | ~6 hours (per task) |

## 6. Risks and Mitigations

| ID | Risk | Impact | Mitigation |
|---|---|---|---|
| R1 | Free-tier model slow (20–45 s) | Test run time grows | Timeout 300 s in newman; documented as BUG-006 |
| R2 | OpenRouter limit 50 req/day | Cannot re-run full suite | Summary committed; full log in `.gitignore` (BUG-005) |
| R3 | 23 MB dataset does not fit 2 GB RAM | Cannot test full dataset | 650 KB representative sample (BUG-001) |
| R4 | Model hallucinates function names | Wrong tool_call | Pinned model; documented as BUG-004 |
| R5 | 5000-char input → timeout | A14 fails | Documented as BUG-007 (Low/Low, edge case) |
| R6 | Network to VPS: 160 ms ping, 25–50% loss | Response time grows | Retries, generous timeouts |

## 7. Entry Criteria

- Container `spring-ai-chat-spring-ai-app` is `Up (healthy)`.
- `GET /api/health` → `200 OK`, body `OK`.
- Dataset loaded and indexed: 468 chunks.
- Tools available: `curl`, `jq`, `docker`, `postman/newman` image pulled.

## 8. Exit Criteria

- 100% planned test cases executed (A1–A15 + 20 manual RAG + 12 JUnit).
- All defects logged with correct severity/priority.
- Test Summary Report prepared and committed.
- No critical open defect blocking the core function.
- Artifacts pushed to GitHub: `qa/` folder + link in `README-level-3.md`.

## 9. Schedule (6 hours)

| Phase | Duration | Deliverable |
|---|---|---|
| Planning | 0:30 | `qa/test-plan.md` |
| Test design | 1:00 | `qa/test-design.md` (4 techniques) |
| API test execution | 0:30 | `qa/api-tests.md` + `newman-summary.json` |
| Manual RAG execution | 2:00 | 20/20 results |
| Bug reporting | 0:30 | `qa/bug-reports.md` |
| Summary + commit | 1:30 | `qa/summary.md`, GitHub push |
| **Total** | **6:00** | |

## 10. Deliverables (Artifacts)

| Artifact | Path |
|---|---|
| Test Plan | `qa/test-plan.md` (this file) |
| Test Design (4 techniques) | `qa/test-design.md` |
| API test cases + results | `qa/api-tests.md` |
| Bug reports (BUG-001…007) | `qa/bug-reports.md` |
| Test Summary Report | `qa/summary.md` |
| Postman collection | `qa/postman/spring-ai-chat.postman_collection.json` |
| Newman summary | `qa/postman/newman-summary.json` |
| Newman full log | `qa/postman/newman-report.json` (`.gitignore`) |
| JUnit results | `README-level-3.md` → JUnit Tests |
| Main README link | `README-level-3.md` → API/HTTP Testing (Postman) |

---

**End of Test Plan.**
