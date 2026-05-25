# Kotlin 마이그레이션 가이드

## 적합한 용도

- 기존 **Gradle 멀티모듈** 에 JVM 언어만 추가하고 싶을 때
- Java 도메인 코드와 **100% 상호운용** 이 필요할 때 (DataProvider, Kylin 클라이언트 등)
- 코루틴 기반 **배치·스케줄** 작업

## 프로젝트 배치 예

```
modules/
  domain/                        # Java (유지)
  insight-board-kotlin-batch/    # 신규 Kotlin 모듈
    src/main/kotlin/.../jobs/
```

`settings.gradle.kts`:

```kotlin
include("modules:insight-board-kotlin-batch")
```

## Boot 4 + Kotlin

`build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
}

dependencies {
    implementation(project(":modules:application"))
    implementation("org.springframework.boot:spring-boot-starter")
}
```

- Spring Boot 4 는 Kotlin 2.x + `kotlin-spring` 플러그인 권장
- Jackson Kotlin 모듈: `com.fasterxml.jackson.module:jackson-module-kotlin`

## JSON (Jackson)

Java `JsonMapper` 와 동일 ObjectMapper 설정을 Kotlin 에서 재사용:

```kotlin
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class BoardResponse(
    val id: Long,
    val name: String,
    val layoutJson: String?,
)

val mapper = jacksonObjectMapper()
val boards: List<BoardResponse> = mapper.readValue(httpBody)
```

**주의**: `data class` 필드명은 API JSON 과 일치시키거나 `@JsonProperty` 사용.

## API 클라이언트 (Ktor / WebClient)

```kotlin
// 의사 코드 — 세션 쿠키 유지
val client = HttpClient(CIO) {
    install(HttpCookies)
}
val login = client.post("http://localhost:8081/api/v1/auth/login") {
    contentType(ContentType.Application.Json)
    setBody("""{"userId":"admin01","password":"admin123"}""")
}
val boards = client.get("http://localhost:8081/api/v1/boards")
```

## DB

- **권장**: Kotlin 배치가 DB에 직접 접근하지 않고 **Boot API** 를 호출
- 직접 접근 시: Exposed/Flyway Java 마이그레이션 공유 (`db/migration`)

## TDD

- `kotlin.test` + JUnit 5 (`@Test` JVM)
- 도메인 로직은 Java `LoginUseCase` 를 그대로 호출해 중복 최소화

## 이전 순서 제안

1. 읽기 전용 API 클라이언트 (Board/Dataset)
2. Kotlin 배치 모듈에서 Job 스케줄
3. 필요 시 컨트롤러 일부를 Kotlin 으로 작성 (Java와 공존)
