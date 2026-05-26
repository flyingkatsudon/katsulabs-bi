# Python 마이그레이션 가이드

## 적합한 용도

- ETL·데이터 품질 검사·ML 추론 등 **레거시 UI 밖** 배치
- Jupyter 기반 프로토타입
- Postgres 메타 DB에 대한 **읽기 전용** 리포트

**비권장**: Spring Security 세션을 Python 으로 직접 재현해 전체 BI 스택 대체

## JSON

표준 라이브러리 + **Pydantic v2** (Jackson DTO 와 필드 정렬):

```python
from datetime import datetime
from pydantic import BaseModel
import httpx

class BoardResponse(BaseModel):
    id: int
    name: str
    layout_json: str | None = None
    created_at: datetime | None = None

    class Config:
        populate_by_name = True
        alias_generator = lambda s: "".join(
            word if i == 0 else word.capitalize()
            for i, word in enumerate(s.split("_"))
        )  # snake_case API 어댑터용 — 실제 API는 camelCase

# API는 camelCase 이므로 model_config:
# model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
```

실무에서는 `alias` 를 API 스펙에 맞게 명시:

```python
class BoardResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: int
    name: str
    layout_json: str | None = Field(None, alias="layoutJson")
```

## API 클라이언트 (httpx)

```python
import httpx

BASE = "http://localhost:8081/api/v1"

with httpx.Client() as client:
    login = client.post(
        f"{BASE}/auth/login",
        json={"userId": "admin01", "password": "admin123"},
    )
    login.raise_for_status()
    boards = client.get(f"{BASE}/boards")
    boards.raise_for_status()
    data = boards.json()
```

세션 쿠키는 `httpx.Client` 가 jar 에 유지합니다.

## DB 직접 접근

Flyway 마이그레이션과 동일 스키마 (`dashboard_*` 테이블).

```python
# psycopg (PostgreSQL)
import psycopg

conn = psycopg.connect("postgresql://katsulabs_bi:katsulabs_bi@localhost:5432/katsulabs_bi")
```

- **쓰기**는 Boot API 를 통하는 것을 권장 (RBAC·감사 로그 일관성)
- H2 로컬은 Python 보다 **API 통합 테스트** 용도

## Jackson ↔ Python 매핑

| Java (Jackson) | Python |
|----------------|--------|
| `JsonMapper.toJson` | `model.model_dump_json()` |
| `Instant` ISO-8601 | `datetime.fromisoformat` |
| `null` 필드 생략 | `model_dump(exclude_none=True)` |

## 테스트 (pytest)

```python
def test_board_model():
    raw = {"id": 1, "name": "Demo", "layoutJson": None}
    board = BoardResponse.model_validate(raw)
    assert board.name == "Demo"
```

## 배포 패턴

```
[Airflow/Prefect] → HTTP → [Boot API]
                  ↘ SQL → [Postgres read replica]
```

OpenAPI 스펙이 생기면 `openapi-python-client` 로 클라이언트 자동 생성을 권장합니다 (Phase 2).
