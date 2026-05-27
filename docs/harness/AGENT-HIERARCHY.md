# Agent hierarchy

Katsulabs BI의 에이전트 계층과 위임 정책입니다.

## 계층도

```
┌─────────────────────────────────────────────────────────────┐
│  Main agent (Composer)                                       │
│  Rules: 00-orchestrator, 10-harness, AGENTS.md, User Rules   │
│  Delegates via Task tool or .cursor/agents/                  │
└───────────────┬─────────────────────────────────────────────┘
                │
    ┌───────────┼───────────┬──────────────┬─────────────────┐
    ▼           ▼           ▼              ▼                 ▼
 explore      shell    ci-investigator  legacy-refactor   flyway-seed
 (readonly)  (git/ci)  (PR CI 1건)    (custom)          (custom)
    │           │           │              │                 │
    └───────────┴───────────┴──────────────┴─────────────────┘
                │
                ▼
         CI (.github/workflows/ci.yml) — merge 게이트
```

## 역할 정의

| 계층 | 식별 | 책임 | 쓰기 |
|------|------|------|------|
| **Main** | Composer / Agent 모드 | 계획, 위임, 통합, PR·커밋(사용자 요청 시) | ✅ |
| **explore** | `Task` + `subagent_type=explore` | 넓은 코드 탐색, 파일 목록, 패턴 검색 | ❌ readonly |
| **shell** | `Task` + `shell` | git status, gradlew, npm, 스크립트 | ✅ |
| **ci-investigator** | `Task` + `ci-investigator` | 단일 실패 CI job 원인 요약 | 읽기 위주 |
| **legacy-refactor** | `.cursor/agents/legacy-refactor.md` | Legacy 브릿지·`dataJson` 단계적 제거 | ✅ |
| **flyway-seed** | `.cursor/agents/flyway-seed.md` | Flyway 시드·데모 보드 | ✅ |

## 위임 결정표 (메인 에이전트)

| 상황 | 위임 | 이유 |
|------|------|------|
| “어디에 X가 있지?” / 5파일 이상 탐색 | `explore` | 컨텍스트 격리 |
| git / `./gradlew` / `npm` 반복 | `shell` | 터미널 특화 |
| PR CI 한 job 실패 | `ci-investigator` | 짧은 RCA |
| `Legacy*`, `dataJson`, compat 레이어 | `legacy-refactor` | PR-B~G 순서 |
| `V*__*.sql` 시드·Chart Gallery | `flyway-seed` | Flyway·데모 계약 |
| 단일 파일 버그·명확한 API 수정 | **메인 직접** | 오버헤드 방지 |
| 사용자가 “병렬로” 요청 | 동일 메시지에 여러 `Task` | 병렬 정책 |

## 위임하지 말 것

- 사용자가 **명시적으로** 메인에게 요청한 작업
- 1~2파일만 읽으면 되는 질문 (`Read` / `Grep` 직접)
- **커밋·push·PR 생성** — 메인만 (User Rules)
- 보안·비밀·`.env` 커밋

## 서브에이전트 prompt 템플릿

```markdown
## Context (필수)
- 저장소 루트의 AGENTS.md 를 읽습니다.
- 아키텍처: docs/migration/03-목표-아키텍처.md
- [Legacy 작업인 경우] docs/migration/04-legacy-layer-removal.md 의 PR 순서를 따릅니다.

## Task
[구체적인 목표, 수정할 파일 경로, 완료 기준을 적어 주세요]

## Constraints
- 변경은 최소화하고, 기존 컨벤션을 따릅니다.
- 메인 에이전트가 명시적으로 요청하지 않는 한 커밋하지 않습니다.
- 수정 범위에 따라 ./gradlew test 와/또는 cd frontend && npm test 를 실행합니다.
```

## 커스텀 서브에이전트

| name | 파일 | 트리거 키워드 |
|------|------|----------------|
| `legacy-refactor` | `.cursor/agents/legacy-refactor.md` | Legacy, dataJson, compat, PR-B~G |
| `flyway-seed` | `.cursor/agents/flyway-seed.md` | Flyway, seed, Chart Gallery, demo board |

내장 `explore` / `shell` / `ci-investigator`는 Cursor가 제공합니다. 커스텀 에이전트와 **이름이 겹치지 않게** 유지하세요.

## 검증 책임

| 변경 | 최소 검증 |
|------|-----------|
| `modules/**` | `./gradlew test` |
| `frontend/**` | `cd frontend && npm test` |
| Flyway | 기동 + Chart Gallery / Demo Board 수동 또는 통합 테스트 |
| package | CI `package` job (bootJar + static) |

## 정책 우선순위 (충돌 시)

1. 사용자 명시 지시
2. User Rules (git/PR 안전)
3. `00-orchestrator.mdc`
4. `AGENTS.md`
5. path rules (`backend` / `frontend`)
6. `docs/migration/*`
