# CBoard 보안 검증 체크리스트 (dashboard 모듈용)

중국계 OSS 의존성 우려에 대한 **실행 가능한 검증 목록**입니다.  
결론: **TuiQiao/CBoard 바이너리를 그대로 쓰지 않고**, `dashboard` 모듈 재구현 시 아래를 CI에서 강제합니다.

---

## 1. 업스트림 CBoard (TuiQiao/CBoard `branch-0.4.2`) 스냅샷

| 항목 | 결과 |
|------|------|
| SECURITY.md | **없음** |
| GitHub Security Advisories | **0건 공개** (미등록 가능성) |
| 최근 커밋 | 2025-12 경 (저장소 활동은 있음) |
| 라이선스 | Apache 2.0 |
| JDK / Spring | 8 / 4.3.7 |
| fastjson | `1.2.29.sec10` (여전히 fastjson 계열) |
| Alibaba Druid | 1.1.12 |
| Maven 저장소 | **Aliyun mirror 기본** |

## 2. BDP 레거시 포크 추가 위험

| 항목 | BDP `pom.xml` | 위험도 |
|------|---------------|--------|
| fastjson | 1.2.29 (sec10 아님) | 🔴 Critical |
| log4j | 1.2.17 | 🔴 Critical |
| druid | 1.0.27 | 🟠 High |
| Tomcat deploy credentials | 평문 in POM | 🔴 Critical |
| Aliyun repository | HTTP mirror | 🟠 High |

## 3. Deny list (dashboard 모듈 빌드 금지)

```text
com.alibaba:fastjson
com.alibaba.fastjson2 (별도 승인 전)
com.alibaba:druid
log4j:log4j
commons-collections:commons-collections:3.*
com.caucho:hessian (사용처 없으면)
```

## 4. Allow / 대체

| 용도 | 허용 |
|------|------|
| JSON | `com.fasterxml.jackson` (Boot BOM) |
| Connection pool | `com.zaxxer:HikariCP` |
| Excel | Apache POI 최신 BOM |
| CSV | Apache Commons CSV |
| 표현식 | Aviator 사용 시 **샌드박스·화이트리스트** 문서화 후만 |

## 5. 프론트엔드 (구 CBoard static)

| 자산 | 조치 |
|------|------|
| `webapp/cboard/lib/*.min.js` | **배포 금지** |
| ECharts / Baidu Map 등 | npm 공식 패키지 + lockfile + `npm audit` |
| AngularJS | 사용 금지 |

## 6. CI 파이프라인 (필수)

```bash
./gradlew dependencyCheckAnalyze   # OWASP
./gradlew cyclonedxBom
./gradlew test
```

- PR 시: deny list dependency 발견 시 **빌드 실패**
- 릴리스 시: Trivy + SBOM 아카이브

## 7. 런타임 하드닝 (app 모듈)

- JWT only, CSRF N/A (stateless API)
- DataProvider JDBC: **read-only 계정** 기본
- 외부 OLAP(Kylin/Presto): 별도 VPC·SG
- Secret: AWS Secrets Manager (로컬 `.env` gitignore)

## 8. 인도적 검증 결론

| 질문 | 답 |
|------|-----|
| CBoard를 Maven 라이브러리로 “업데이트”만 하면 안전한가? | **아니오** |
| 중국계 라이브러리를 반드시 제거해야 하는가? | **fastjson·druid·aliyun mirror는 제거**; 국적이 아니라 **CVE·역직렬화·공급망** 기준 |
| 안전한 경로는? | **`dashboard` 재구현 + SBOM + deny list** |

검증 완료 시 이 문서 하단에 **날짜·담당·스캔 리포트 링크**를 기록합니다.
