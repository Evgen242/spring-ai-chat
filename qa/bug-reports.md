# Bug Reports — Spring AI Chat RAG

| ID | Title | Steps | Expected | Actual | Severity | Priority | Status |
|---|---|---|---|---|---|---|---|
| BUG-001 | 23 MB dataset does not fit VPS 2 GB RAM | Load 23 MB → indexing | Indexing completes | CPU 100%, swap 100%, VPS unusable | High | High | Open (infra limit) |
| BUG-002 | Morphology fails on complex cases | Query "Тарновыми" | Finds Tarnova | Not found | Medium | Low | Open |
| BUG-003 | `sources` non-empty on refusal | Out-of-scope question | `sources: []` | Top-10 irrelevant chunks | Medium | Medium | **Fixed** |
| BUG-004 | Free model hallucinates function names | Function request | Correct tool_call | `example_function_name` | High | High | Open (model) |
| BUG-005 | OpenRouter free-tier 50 req/day | >50 requests/day | Stable work | 429 / refusals | High | High | Known (provider) |
| BUG-006 | Slow response 20–45s | Any RAG request | Acceptable time | 20–45s | Medium | Medium | Open (free-model) |
| BUG-007 | 5000-char request → timeout (300s) | POST 5000 'a' chars | 200/4xx within 300s | ESOCKETTIMEDOUT, no response | Low | Low | Open (free-model) |

## BUG-007 — detail

**Reproduction:**

```bash
MSG=$(python3 -c 'print("a"*5000)')
curl -X POST http://194.154.27.141:8082/api/rag \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"$MSG\"}"
```
Actual: no response within 300 s (ESOCKETTIMEDOUT).

Root cause (hypothesis): free-tier OpenRouter queues/aborts large noisy prompts; internal RestTemplate timeout is shorter than the client timeout, and Spring does not surface an error in time.

Impact: edge case only — normal requests (< 200 chars) work fine (A1–A13, A15 PASS).

Recommendation:

Cap message length on input (@Size(max=2000)).

Explicit RestTemplate timeout → return 504 on upstream timeout.

Paid model or dedicated inference (Ollama/vLLM) for stability.

Severity: Low. Priority: Low.
