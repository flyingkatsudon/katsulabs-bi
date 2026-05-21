# CBoard 로컬 검증 실행 가이드

이 문서는 **cboard(BDP) 라이브러리가 로컬에서 정상 기동·로그인·대시보드 진입이 되는지** 확인하는 절차를 정리합니다.  
Cloud Agent 환경에서는 MySQL/PostgreSQL 원격 DB 없이 **H2** 로 대체하고, 로그인은 **개발용 계정** 으로 강제합니다.

## 사전 요구 사항

| 항목 | 버전/비고 |
|------|-----------|
| JDK | 21 (런타임). 컴파일 타겟은 Java 8 |
| Maven | 3.9+ (`~/.local/maven` 설치 가능) |
| ffmpeg | 데모 영상 MP4 변환용 (선택) |
| Playwright | 데모 영상 녹화용 (선택) |

## 1. 빌드

```bash
export PATH="$HOME/.local/maven/bin:$PATH"
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.util=ALL-UNNAMED \
  --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"

cd /workspace
mvn clean package -DskipTests -Denv=local
```

`-Denv=local` 프로파일은 `src/main/resources/local/` 설정을 classpath 에 복사합니다.

- 메타 DB: H2 파일 `./data/cboard_meta` (MySQL 호환 모드)
- 리포트 DB 스텁: H2 인메모리 `cboard_report`
- 집계 H2: `./data/cboard_agg`

## 2. 서버 기동

```bash
mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Denv=local
```

- URL: http://127.0.0.1:8080/bdp/
- 컨텍스트 경로: `/bdp`

또는:

```bash
bash scripts/run-local.sh
```

### IntelliJ IDEA에서 `tomcat7:run` 실행 시

**증상:** `Cannot load configuration class: org.cboard.controller.WebConfig` + `InaccessibleObjectException: module java.base does not "opens java.lang"`.

**원인:** Java 17+ 에서 Spring 4 CGLIB 이 `--add-opens` 없이 동작하지 않음. `systemProperties` 로 넣은 `MAVEN_OPTS` 는 Tomcat JVM 에 적용되지 않습니다.

**해결:** `tomcat7-maven-plugin` 2.2 는 `jvmArgs` 파라미터를 지원하지 않습니다. Tomcat 은 Maven 과 **같은 JVM** 에서 실행되므로 아래 중 하나를 적용하세요.

1. 프로젝트 루트 `.mvn/jvm.config` (이미 포함)
2. IntelliJ Maven Run → **Runner** → **VM Options** (아래 3줄)

**추가로 IntelliJ Maven Run 설정에 VM options** (권장):

```
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
```

설정 위치: Run/Debug Configurations → 해당 Maven 실행 → **Runner** 탭 → **VM Options**

`.mvn/jvm.config` 파일도 Maven 프로세스용으로 동일 옵션을 포함합니다.

**module-info.class SEVERE 로그:** Tomcat 7 이 JAXB 등 Java 9+ JAR 의 `module-info.class` 를 스캔할 때 나는 경고이며, Spring 컨텍스트가 정상 기동되면 무시해도 됩니다.

**`ParamVO` / `GeneralAnalyticsBiz` 기동 실패:** `-Denv=local` 없이 실행하면 PostgreSQL 리포트 MyBatis XML 과 `com.shinhan.ctrl` 컨트롤러가 로드됩니다. 반드시 아래를 실행하세요.

```bash
mvn clean compile -Denv=local
mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Denv=local
```

로컬 오버레이(`src/main/resources/local/`)가 `spring-mybatis.xml`, `spring.xml`, `application-config.xml` 을 CBoard 전용으로 덮어씁니다. MVC 는 `WebConfig` 가 `dev.auth.enabled=true` 일 때 `ShinhanMvcConfig`(리포트 컨트롤러)를 자동으로 비활성화합니다.

IntelliJ Maven Run **Parameters** 에 `-Denv=local` 이 있는지 확인하세요.

## 3. 로그인 (개발용)

로그인 화면: http://127.0.0.1:8080/bdp/login.jsp

| 필드 | 값 |
|------|-----|
| 그룹사 (v0) | `SY` (신한DS) |
| 사번 (v1) | `000admin` (또는 `admin` — 6자 미만은 dev 모드에서 `000admin` 으로 매핑) |
| 비밀번호 (v2) | `qwerty!23` |

`src/main/resources/local/config.properties`:

```properties
dev.auth.enabled=true
dev.auth.bypass=true
```

DB 연결 실패 시에도 `dev.auth.bypass=true` 이면 위 계정으로 인증 우회가 가능합니다.

### curl 로 세션 확인

```bash
curl -c /tmp/cookies.txt -X POST "http://127.0.0.1:8080/bdp/process" \
  -d "v0=SY&v1=000admin&v2=qwerty%2123" -L

curl -b /tmp/cookies.txt http://127.0.0.1:8080/bdp/cboard/starter.jsp
```

성공 시 `starter.jsp` (AngularJS CBoard UI) 로 리다이렉트됩니다.

## 4. 검증 결과 (요약)

| 항목 | 상태 |
|------|------|
| Maven WAR 빌드 (`-Denv=local`) | OK |
| Spring 컨텍스트 기동 (H2 + Ehcache + JAXB) | OK |
| 로그인 → `cboard/starter.jsp` | OK |
| 데모 영상 | `docs/videos/cboard-verification-demo.mp4` |

## 5. 데모 영상 녹화

서버 기동 후:

```bash
pip install playwright
python3 -m playwright install chromium
python3 scripts/record-demo-video.py
ffmpeg -y -i docs/videos/cboard-verification-demo.webm \
  -c:v libx264 -pix_fmt yuv420p docs/videos/cboard-verification-demo.mp4
```

## 6. Connection refused 대응

원본 `config.properties` 는 MySQL(`127.0.0.1:3306`) 및 외부 PostgreSQL 을 가리킵니다.  
로컬 검증 시:

1. **권장**: `-Denv=local` (H2, 내장 스키마/시드)
2. **대안**: `docker-compose.yml` (추가 시) 로 MySQL 8 기동 후 `jdbc.mysql.url` 만 교체

PostgreSQL 리포트 매퍼는 로컬에서 H2 스텁 DB 로 기동만 보장합니다(차트 데이터는 비어 있을 수 있음).

## 7. 누락 의존성 (owlnest)

신한 커스텀 JAR `com.owlnest.*` 는 저장소에 없어, 로컬 검증용 스텁을 `src/main/java/com/owlnest/` 에 두었습니다.  
운영/통합 환경에서는 실제 JAR 을 `lib/` 또는 사설 Maven 저장소에 배치해야 합니다.

## 8. `com.shinhan.vo.ParamVO` / PostgreSQL MyBatis 오류 (로컬)

**증상:** `ClassNotFoundException: com.shinhan.vo.ParamVO` 또는 `postgreSqlSessionFactory` 초기화 실패.

**원인:** 로컬 H2 환경에서도 `mapper/report/*.xml` (신한 리포트용 PostgreSQL) 이 기동 시 로드됨.

**해결:** `-Denv=local` 시 `spring-mybatis.xml` 이 PostgreSQL 스캔 없이 CBoard 메타 DB 만 사용하도록 오버레이됩니다. 최신 코드 pull 후 `mvn clean compile -Denv=local` 후 재실행하세요.

신한 리포트 메뉴(`/report/**`)는 로컬 H2 스텁에서는 데이터가 없을 수 있습니다. CBoard 대시보드(`/cboard/**`) 검증이 목적입니다.

## 9. 로그인 후 쿼리 오류 (create_time / update_time)

증상: `Column "A.CREATE_TIME" not found` 등 MyBatis SQL 오류.

원인: 로컬 H2 스키마에 CBoard 0.4 패치 컬럼(`create_time`, `update_time`) 누락.

조치: `schema-h2.sql` 및 기동 시 `schema-h2-patch.sql` 이 자동 적용됩니다.  
이미 생성된 `./data/cboard_meta.mv.db` 가 있다면 서버 재기동만으로 패치가 적용되며, 계속 실패하면 `rm -rf data` 후 재기동하세요.

## 10. 알려진 제한

- Tomcat 7 + Spring 4.3 은 Java 21 에서 `--add-opens` JVM 옵션이 필요합니다.
- 로그인 POST 직후 HTTP 302 → `login.jsp` → `starter.jsp` 체인은 정상이나, 중간 응답 코드가 302/500 으로 보일 수 있습니다(핸들러 forward 이슈). 세션 쿠키 기준 UI 접근은 정상입니다.
- PhantomJS, Redis, 외부 분석 API 는 로컬에서 비활성/스텁 처리됩니다.

## 9. 다음 단계 (현대화)

`docs/MODERNIZATION_ROADMAP.md` 참고 — Java 21, Spring Boot 3, React, 멀티모듈 `insight-board`, 모니터링 스택 등.
