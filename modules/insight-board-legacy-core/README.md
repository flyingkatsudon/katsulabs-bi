# insight-board-legacy-core

레거시 `src/main/java` 전체를 JAR로 컴파일해 `DataProviderService` 등 aggregate 엔진을 Boot에 붙이기 위한 실험 모듈입니다.

**현재 기본 Gradle 빌드에는 포함되지 않습니다.** (Spring Security 4, AspectJ, CAS 등 Boot 4와 클래스패스 충돌)

활성화 시 `settings.gradle.kts`에서 `include("modules:insight-board-legacy-core")` 주석을 해제하고, `build.gradle.kts` 의존성을 `pom.xml` 기준으로 보완한 뒤 `AggregateQueryPort` 구현체를 API 모듈에 등록하세요.
