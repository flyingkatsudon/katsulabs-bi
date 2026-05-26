# Legacy 호환 레이어 제거 로드맵

> Katsulabs BI는 DB 스키마 v2(`ib_*` 정규화)를 쓰지만, API·UI 일부는 여전히 v1 `data_json` 문자열 계약을 사용합니다.  
> `Legacy*` 이름의 코드는 “옛 제품 소스”가 아니라 **현재 동작에 필요한 브릿지**인 경우가 많습니다.

## 현재 Legacy 계층 요약

| 구성요소 | 역할 | 제거 가능 시점 |
|----------|------|----------------|
| `LegacyWidgetDataJson` / `LegacyDatasetDataJson` | 정규화 테이블 ↔ API `dataJson` 변환 | API·프론트가 구조화 필드만 쓸 때 |
| `LegacySha256PasswordHasher` | 시드/기존 사용자 비밀번호(SHA-256 HEX) 검증 | 해시 마이그레이션 또는 전원 재설정 후 |
| `legacyChartEcharts.ts` | 구 `chartType` → ECharts 옵션 | 차트 타입 enum·시드 데이터 정리 후 |
| `legacyBodyClass.ts` / `LegacyBodyClassSync` | AdminLTE `body` CSS 클래스 (UI) | AdminLTE 제거 또는 CSS 모듈 전환 시 |
| ~~세션 `CBoard_USER`~~ | 구 세션 키 읽기 | **제거됨** (`KATSULABS_BI_USER`만 사용) |

## PR 단위 제안

### PR-A — 완료: 구 세션 키 제거

- `SessionAuthenticationFilter`: `KATSULABS_BI_USER`만 사용
- 영향: 이전 `CBoard_USER` 세션은 무효 → **재로그인** 필요 (로컬 dev면 무시 가능)

### PR-B — 명명 정리 (동작 변경 없음)

목적: `Legacy*`가 “곧 삭제”로 오해되지 않도록 이름만 정리.

| 현재 | 제안 |
|------|------|
| `LegacyWidgetDataJson` | `WidgetDataJsonMapper` |
| `LegacyDatasetDataJson` | `DatasetDataJsonMapper` |
| `LegacyBodyClassSync` | `AdminLteBodyClassSync` |
| `legacyBodyClass.ts` | `adminLteBodyClass.ts` |

### PR-C — Widget API: `dataJson` → 구조화 DTO (1차)

1. **API**: `WidgetResponse`에 `bindings[]`, `datasetId` 등 필드 추가 (기존 `dataJson` 유지·deprecated)
2. **프론트**: `WidgetWorkbenchPage`가 새 필드 우선 사용, `dataJson`은 fallback
3. **인프라**: `WidgetRepositoryImpl`이 mapper row를 직접 DTO로 매핑 (`compose`는 deprecated 경로만)

검증: Widget CRUD E2E, Chart Gallery 보드 렌더.

### PR-D — Dataset API: `dataJson` → 구조화 DTO (1차)

PR-C와 동일 패턴 (`columns[]`, `sqlText` 등).

### PR-E — `dataJson` 컬럼/API 필드 제거 (2차)

- DB: `ib_widget.data`, `ib_dataset.data` 사용 중단 (또는 nullable 유지·미사용)
- API: `dataJson` 필드 삭제
- `Legacy*DataJson` 클래스 삭제

전제: PR-C/D 완료, Flyway 시드·데모 보드가 새 API만 사용.

### PR-F — 비밀번호 해시 현대화

1. `PasswordHasher`에 알고리즘 식별자(예: `{sha256}`, `{bcrypt}`) 또는 별도 컬럼
2. 로그인 성공 시 SHA-256 → bcrypt 재해시 (선택)
3. `LegacySha256PasswordHasher` 제거 또는 “검증 전용”으로 축소

### PR-G — 차트 타입 정리

1. 허용 `chartType` 목록을 문서·시드에 고정
2. `legacyChartEcharts`를 `chartRender`에 통합 또는 삭제
3. 데모 위젯 JSON을 v2 chartType만 사용하도록 Flyway patch

## 권장 순서

```
PR-A (세션) → PR-B (이름) → PR-C/D (구조화 API) → PR-E (dataJson 삭제) → PR-F (비밀번호) → PR-G (차트)
```

PR-B는 언제든 병렬 가능. PR-C/D는 도메인별로 나눠 리뷰하기 좋습니다.

## 완료 기준 (Legacy 레이어 “0”에 가깝게)

- [ ] API 요청/응답에 `dataJson` 없음
- [ ] `infrastructure/.../compat/*DataJson` 삭제
- [ ] 로그인 해시가 SHA-256 단일 경로가 아님 (또는 문서화된 마이그레이션 완료)
- [ ] `legacyChartEcharts` 미사용
- [ ] 코드·문서에 `CBoard` / `cboard` 문자열 없음 (과거 git archive 스크립트 주석 제외)
