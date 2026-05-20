# BDP Legacy (단독 실행)

레거시 WAR 프로젝트입니다. CBoard BI(`org.cboard`) + 신한 커스텀(`com.shinhan`)이 포함됩니다.

신규 개발은 [insight-board/](../insight-board/README.md)를 사용하세요.

## 요구 사항

- JDK 8+
- Maven 3.6+
- (선택) Tomcat 7+ 또는 `mvn tomcat7:run`

## 빌드

```bash
cd legacy
mvn clean package -DskipTests
```

산출물: `target/bdp.war`

## 로컬 실행 (Tomcat 플러그인)

```bash
cd legacy
mvn tomcat7:run
```

기본 포트는 `8080`입니다. 애플리케이션 컨텍스트는 `pom.xml`의 `finalName` 설정을 따릅니다.

## DB 스키마

- `sql/mysql/`, `sql/oracle/`, `sql/sqlserver/` — CBoard 메타데이터 DDL
- `src/main/resources/h2/`, `h2-demo/` — 로컬 H2 프로필

## 디렉터리

| 경로 | 설명 |
|------|------|
| `src/main/java/org/cboard/` | CBoard BI 엔진 |
| `src/main/java/com/shinhan/` | 신한 인사이트·리포트 |
| `src/main/webapp/cboard/` | AngularJS SPA |
| `src/main/webapp/report/` | 정적 리포트 페이지 |
