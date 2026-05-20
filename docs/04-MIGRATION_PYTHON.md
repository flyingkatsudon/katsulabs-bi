# Python 마이그레이션 가이드

`insight-board` 백엔드를 **FastAPI** 기반으로 이전할 때의 실무 가이드입니다.

---

## 1. 목표 아키텍처

```
bdp-python/
├── app/
│   ├── main.py              # FastAPI app
│   ├── api/v1/              # 라우터 (Java Controller 대응)
│   ├── domain/              # Pydantic models (= Java domain POJO)
│   ├── services/            # 비즈니스 로직
│   ├── repositories/        # SQLAlchemy 2.0 async
│   └── core/
│       ├── config.py
│       ├── security.py      # JWT (PyJWT / python-jose)
│       └── deps.py
├── alembic/                 # Flyway 대응
├── tests/
└── pyproject.toml           # Poetry or uv
```

**권장 스택:** Python 3.12+, FastAPI 0.115+, SQLAlchemy 2.0, Alembic, Pydantic v2, `httpx` (테스트), `uvicorn`.

---

## 2. OpenAPI 계약 이전

1. Java: `./gradlew bootRun` 후 `http://localhost:8080/v3/api-docs` JSON export → `contracts/openapi.json`
2. Python: `openapi-generator` 또는 수동으로 Pydantic 스키마 생성
   ```bash
   datamodel-code-generator --input openapi.json --output app/domain/schemas.py
   ```
3. **엔드포인트 경로·HTTP 메서드·상태 코드를 Java와 동일**하게 유지 → React 클라이언트 재사용

---

## 3. 레이어별 매핑

| Java (insight-board) | Python |
|-----------------|--------|
| `@RestController` | `APIRouter` |
| `@Service` | `services/*.py` 함수/클래스 |
| JPA `Entity` | SQLAlchemy `DeclarativeBase` |
| `JwtAuthenticationFilter` | `Depends(get_current_user)` |
| Flyway `V*.sql` | Alembic revision |
| `@WebMvcTest` | `pytest` + `TestClient` |
| Lombok | Pydantic `BaseModel` |

---

## 4. 인증 (JWT)

```python
# app/core/security.py (개념)
def create_access_token(sub: str) -> str: ...
def verify_token(token: str) -> dict: ...

# app/api/v1/auth.py
@router.post("/login")
async def login(body: LoginRequest, db: AsyncSession = Depends(get_db)):
    user = await authenticate(db, body.username, body.password)
    return TokenResponse(access_token=..., refresh_token=...)
```

- 비밀번호: `passlib[bcrypt]`
- Refresh 토큰: Redis 또는 DB `refresh_tokens` 테이블

---

## 5. 데이터베이스

| 환경 | 연결 |
|------|------|
| Local | `sqlite+aiosqlite:///./bdp.db` 또는 H2 대신 PostgreSQL Docker |
| Prod | `postgresql+asyncpg://...` |

**H2 → Python:** 로컬은 PostgreSQL Docker 권장 (H2 JDBC 드라이버는 Python에서 비표준).

Flyway SQL을 Alembic으로 변환:
```bash
# 초기 revision에 V1__schema.sql 내용 복사
alembic revision -m "initial"
```

---

## 6. 인사이트 쿼리 이전

레거시 MyBatis XML의 PostgreSQL 함수는:

| PostgreSQL | SQLAlchemy |
|------------|------------|
| `row_number() OVER (...)` | `func.row_number().over(...)` |
| `array_position` | `func.array_position` 또는 Python 정렬 |
| 동적 `IN` 리스트 | `column.in_(kwd_list)` |

복잡 리포트는 **단계적 이전**: Java에서 검증된 SQL을 `.sql` 파일로 두고 `text()` 실행 후 → ORM 리팩터.

---

## 7. 배치·크롤링 (Python 강점)

- `APScheduler` 또는 Celery + Redis
- Open API 수집: `httpx` async + rate limit (`aiolimiter`)
- 적재: `COPY` / `bulk_insert_mappings`

`docs/06-OPEN_DATA_AND_CRAWLING.md` 파이프라인을 Python 워커로 구현하기 적합.

---

## 8. 테스트 전략

```python
@pytest.fixture
def client():
    with TestClient(app) as c:
        yield c

def test_login(client):
    r = client.post("/api/v1/auth/login", json={"username":"admin","password":"admin"})
    assert r.status_code == 200
    assert "access_token" in r.json()
```

- 커버리지: `pytest-cov`, 목표 80%+
- Contract test: `schemathesis` + OpenAPI

---

## 9. 배포

- Docker: `uvicorn app.main:app --host 0.0.0.0 --port 8000`
- ECS: Java와 동일 Fargate 패턴, 이미지만 교체
- ALB health: `GET /health`

---

## 10. 이전 순서 (권장)

1. OpenAPI export + Auth + Health
2. `dashboard_*` CRUD
3. Report read-only API 1개씩
4. 배치 ingestion
5. CBoard UI는 React가 이미 분리되어 있으면 백엔드만 교체 가능
