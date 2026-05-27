# Agent harness (Katsulabs BI)

에이전트가 **메인 → 서브에이전트**까지 같은 정책으로 동작하도록 하는 설정입니다.

## 구성 요소

| 경로 | 역할 |
|------|------|
| [AGENT-HIERARCHY.md](./AGENT-HIERARCHY.md) | 메인/내장 서브/커스텀 서브 역할·위임 표 |
| [../AGENTS.md](../AGENTS.md) | 실행·데모·supply chain (모든 에이전트 공통) |
| `.cursor/rules/00-orchestrator.mdc` | 메인 오케스트레이션 (항상 적용) |
| `.cursor/rules/10-harness.mdc` | 하네스 문서 포인터 (항상 적용) |
| `.cursor/rules/backend.mdc` | `modules/**` 편집 시 |
| `.cursor/rules/frontend.mdc` | `frontend/**` 편집 시 |
| `.cursor/agents/*.md` | 커스텀 서브에이전트 (Legacy, Flyway) |
| `.cursor/hooks.json` | 파괴적 git 명령 차단 등 |

## 빠른 시작 (사람·에이전트)

1. 작업 전 [AGENTS.md](../../AGENTS.md) 로 로컬 실행 확인
2. 범위가 크면 [AGENT-HIERARCHY.md](./AGENT-HIERARCHY.md) 위임 표 참고
3. Legacy/`dataJson` 터치 시 [04-legacy-layer-removal.md](../migration/04-legacy-layer-removal.md) PR 순서 준수
4. merge 전 `./gradlew test` 및 `cd frontend && npm test`

## 서브에이전트에 맥락 넣기

내장 `Task` 서브는 **대화 히스토리가 없습니다.** 위임 시 prompt에 최소한 다음을 포함하세요:

```
Read AGENTS.md. Follow docs/harness/AGENT-HIERARCHY.md delegation rules.
[구체적 작업 설명]
```

## 관련 문서

- [03-목표-아키텍처.md](../migration/03-목표-아키텍처.md)
- [04-legacy-layer-removal.md](../migration/04-legacy-layer-removal.md)
- [MYBATIS-XML.md](../migration/MYBATIS-XML.md)
