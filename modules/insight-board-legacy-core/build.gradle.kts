plugins {
    id("java")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.6")
    }
}

dependencies {
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.mybatis:mybatis:3.5.16")
    implementation("org.mybatis:mybatis-spring:3.0.4")

    implementation("com.alibaba:fastjson:1.2.83")
    implementation("com.alibaba:druid:1.2.23")
    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("com.googlecode.aviator:aviator:5.4.3")
    implementation("org.ehcache:ehcache:3.10.8")
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("redis.clients:jedis:5.1.0")
    implementation("org.apache.solr:solr-solrj:9.6.1")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("org.apache.commons:commons-dbcp2")
    implementation("org.slf4j:slf4j-api")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.mysql:mysql-connector-j")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("${rootProject.projectDir}/src/main/java"))
        resources.setSrcDirs(listOf("${rootProject.projectDir}/src/main/resources"))
    }
}
