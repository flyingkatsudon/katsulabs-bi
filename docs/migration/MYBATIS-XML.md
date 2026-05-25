# MyBatis XML (`modules/infrastructure/src/main/resources/mapper/*.xml`)

Gradle/Boot 백엔드에서는 **이 XML 파일들이 필요합니다.** 삭제하면 Repository 가 동작하지 않습니다.

| 파일 | 용도 |
|------|------|
| `UserMapper.xml` | 로그인 사용자 조회 |
| `DatasourceMapper.xml` | 데이터소스 CRUD |
| `DatasetMapper.xml` | 데이터셋 CRUD |
| `WidgetMapper.xml` | 위젯 CRUD |
| `BoardMapper.xml` | 보드 CRUD |
| `CategoryMapper.xml` | 카테고리 CRUD |

집계(`/api/v1/aggregate`)는 MyBatis 가 아니라 `JdbcAggregateQueryAdapter` 가 JDBC 로 직접 실행합니다.

레거시 Tomcat WAR 및 `spring-mybatis.xml` 은 저장소에서 제거되었습니다. MyBatis 설정은 `PersistenceConfig` + 위 mapper XML 만 사용합니다.
