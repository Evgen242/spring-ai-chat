
# Отчёт по Уровню 3 — продвинутое ручное тестирование (Spring AI Chat RAG)

**Репозиторий:** https://github.com/Evgen242/spring-ai-chat
**Коммит:** `f1ad587afec144e1c0d90dc710c1392b01fc0c99`
**Папка qa/:** https://github.com/Evgen242/spring-ai-chat/tree/main/qa
**Раздел Testing → API/HTTP Testing (Postman):** https://github.com/Evgen242/spring-ai-chat/blob/main/README-level-3.md#apihttp-testing-postman

---

## Учтены замечания куратора по Уровню 2

### (1) Сверка с требованиями

- Колонка **Technique** в `qa/api-tests.md` — каждый кейс A1–A15 привязан к технике тест-дизайна (EC / BV / DT / ST / Smoke).
- **Scope** в `qa/test-plan.md` — раздел 3: что входит и что не входит в объём.
- **Coverage Summary** в `qa/test-design.md` — раздел 5: таблица «техника → кейсы → PASS/FAIL».

### (2) Проверка подсчётов

Все цифры сверены между тремя источниками:

| Источник | requests | assertions | PASS / FAIL |
|---|---|---|---|
| `qa/api-tests.md` → Summary | — | 30 total, 2 failed | 15 total, 14 PASS, 1 FAIL |
| `qa/postman/newman-summary.json` | **15** (failed **1**) | **30** (failed **2**) | 14 / 1 |
| `qa/summary.md` → Results | — | — | 20 RAG + 12 JUnit + 14 API = **46/47 = 97.9%** |

**Расхождений нет.**

### (3) Фиксация окружения

`qa/test-plan.md` → разделы 5 (Resources) и 6 (Risks and Mitigations):

- **Инфраструктура:** VPS Ubuntu, Docker 29.6.2, контейнер `spring-ai-chat` — `Up (healthy)`.
- **Java:** Temurin 17.0.20.1 (в контейнере).
- **Инструменты:** curl, jq, Docker, Postman/newman, Maven 3.9.6, JUnit 5.
- **Модель:** `nvidia/nemotron-3.5-lightning:free` (OpenRouter, free-tier).
- **Датасет:** 650 KB, 468 чанков (обоснование — `README-level-3.md` → Justification).
- **Ограничения:** пинг до VPS ~160 мс, потери 25–50%, лимит OpenRouter 50 req/day.
- **Воспроизводимость:** одна команда (`qa/README.md`, `qa/api-tests.md`, `qa/RUNBOOK.md`).

---

## Артефакты

| Артефакт | Описание |
|---|---|
| `qa/RUNBOOK.md` | Как воспроизвести прогон (3 варианта: VPS, локально, Postman UI) |
| `qa/test-plan.md` | Тест-план (10 разделов) |
| `qa/test-design.md` | 4 техники: EC (13), BV (10), DT (8), ST (Mermaid stateDiagram) |
| `qa/api-tests.md` | 15 API-кейсов A1–A15 с Technique. 14/15 PASS (93.3%) |
| `qa/bug-reports.md` | 7 багов (BUG-001…007) с severity/priority |
| `qa/summary.md` | Test Summary Report: 20/20 RAG + 12/12 JUnit + 14/15 API = 46/47 (97.9%) |
| `qa/postman/` | Коллекция A1–A15 + newman-summary.json |

---

## Ключевые находки

- **Валидация ввода:** 400/415 за 12–30 мс (A8–A13).
- **BUG-003** (`sources` при отказе) — **Fixed**, подтверждён A6/A7.
- **A14** (5000 символов) — timeout → **BUG-007**, ограничение free-tier OpenRouter. Рекомендация: cap `message` + явный `RestTemplate` timeout → 504.

---

Готов ответить на вопросы.
