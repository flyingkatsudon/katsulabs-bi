plugins {
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.6")
    }
}

dependencies {
    implementation(project(":modules:application"))
    implementation(project(":modules:domain"))

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.flywaydb:flyway-core")

    runtimeOnly("org.flywaydb:flyway-database-postgresql")
}
