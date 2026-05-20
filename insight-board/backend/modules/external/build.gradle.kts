plugins {
    `java-library`
}

dependencies {
    api(project(":modules:common"))
    implementation(project(":modules:api"))

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
