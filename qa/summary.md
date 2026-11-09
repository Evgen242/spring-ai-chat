
# Test Summary Report — Spring AI Chat RAG

**Date:** 2026-11-09
**Object:** Spring AI Chat Application (RAG + Function Calling), VPS `194.154.27.141:8082`
**Tooling:** curl, jq, Docker, Postman (newman), JUnit 5

## Scope

- **RAG:** aggregation (SITE, DEP, role), exact IDs (EMP), morphology, markers, dates.
- **Function Calling:** `getCurrentTime`, `calculate`, `getSystemInfo`.
- **Boundaries:** non-existent IDs/sites/employees → correct refusal.
- **API/HTTP:** 15 requests (A1–A15) via Postman/newman.
- **JUnit:** 12 automated tests (cross-check).

**Out of scope:** load testing, security/auth, 23 MB full dataset (BUG-001).

## Results

| Suite | PASS | Total | % |
|---|---|---|---|
| RAG functional (manual) | 20 | 20 | 100% |
| JUnit (automated) | 12 | 12 | 100% |
| API/HTTP (Postman) | 14 | 15 | 93.3% |
| **Overall** | **46** | **47** | **97.9%** |

## Defects

| Severity | Open | Fixed | Known |
|---|---|---|---|
| High | BUG-001, BUG-004 | — | BUG-005 |
| Medium | BUG-002, BUG-006 | BUG-003 | — |
| Low | BUG-007 | — | — |

## Quality assessment

The RAG service is **functionally fit** on the 650 KB dataset (468 chunks): retrieval, morphology, markers, aggregation, Function Calling, and the API contract all work as specified.

- **No critical open defect** blocks the core flow (A1–A13, A15 PASS; 20/20 manual RAG cases PASS).
- **A14 (5000-char request) → timeout** — edge case, documented as **BUG-007 (Low/Low)**.
- **BUG-003** (`sources` on refusal) — **fixed and verified** via A6/A7.
- Infrastructure limits (BUG-001, BUG-004, BUG-005, BUG-006) are documented in `README-level-3.md` → Known Limitations.

## Recommendations

1. Real embeddings (Ollama/OpenAI) — closes BUG-002.
2. Persistent vector store (pgvector/Qdrant) + 4+ GB RAM — closes BUG-001.
3. Paid/dedicated model — closes BUG-004/005/006.
4. Input cap (2000 chars) + explicit timeout → 504 — closes BUG-007.

## Exit criteria

- ✅ 100% planned test cases executed.
- ✅ All defects logged with severity/priority.
- ✅ API/HTTP testing via Postman + newman artifacts committed.
- ✅ Test Summary Report prepared.
- ✅ No critical open defect blocking core function.
