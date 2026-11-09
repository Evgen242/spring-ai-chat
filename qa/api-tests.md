
API / HTTP Testing — Postman Collection (A1–A15)
Tool: Postman Collection v2.1, executed via postman/newman in Docker.
Target: http://194.154.27.141:8082
Artifacts: qa/postman/spring-ai-chat.postman_collection.json, qa/postman/newman-summary.json

Run command
bash
docker run --rm \
  -v $(pwd)/qa/postman:/etc/newman \
  postman/newman run spring-ai-chat.postman_collection.json \
  -e env.json \
  --timeout-request 300000 \
  --reporters cli,json \
  --reporter-json-export /etc/newman/newman-report.json
Results (final run)
#	Method	Endpoint	Body / Header	Expected	Actual	Status
A1	GET	/api/health	—	200, OK, text/plain	200, OK, 140ms	✅ PASS
A2	POST	/api/rag	{"message":"Кто такой EMP-0222?"}	200, «Полевец», sources>0	200, 3.67kB, 7.3s	✅ PASS
A3	POST	/api/rag	{"message":"Кто работает на SITE-04?"}	200, reply непустой	200, 23.97kB, 41.9s	✅ PASS
A4	POST	/api/rag	{"message":"Который час?"}	200, время	200, 15.58kB, 11.1s	✅ PASS
A5	POST	/api/rag	{"message":"Сколько будет 2 + 3?"}	200, содержит 5	200, 15.22kB, 44.4s	✅ PASS
A6	POST	/api/rag	{"message":"Как приготовить борщ?"}	200, отказ, sources=[]	200, 345B, 17s, sources=[]	✅ PASS (BUG-003)
A7	POST	/api/rag	{"message":"Кто такой EMP-9999?"}	200, отказ, sources=[]	200, 345B, 11.9s, sources=[]	✅ PASS
A8	POST	/api/rag	{}	4xx	400, 103B, 15ms	✅ PASS
A9	POST	/api/rag	{"message":""}	4xx or 200 refusal	400, 103B, 15ms	✅ PASS
A10	POST	/api/rag	not-a-json	400	400, 242B, 18ms	✅ PASS
A11	POST	/api/rag	{"message":null}	4xx	400, 103B, 12ms	✅ PASS
A12	POST	/api/rag	Content-Type text/plain	415/400	415, 339B, 13ms	✅ PASS
A13	GET	/api/rag	—	405	405, 298B, 14ms	✅ PASS
A14	POST	/api/rag	5000 chars 'a'.repeat(5000)	200/4xx within 300s	ESOCKETTIMEDOUT	❌ FAIL → BUG-007
A15	POST	/api/rag	{"message":"Тест 🚀 <>&test"}	200, reply непустой	200, 345B, 26.9s	✅ PASS
Summary
Total: 15 | PASS: 14 (93.3%) | FAIL: 1 — A14 (BUG-007)

Assertions: 30 total, 2 failed (both A14)

Run duration: 7m 41.7s | Avg response: 11.4s (min 12ms, max 44.4s)

Notes
A8–A13 (validation) — 12–30 ms — input validation is fast and correct.

A2–A7, A15 (RAG/Function) — 7–44 s — expected for free-tier model (BUG-006).

A14 — failed on both 120s and 300s runs (no response at all). Documented as BUG-007.

BUG-003 verified fixed: A6/A7 return sources: [].
