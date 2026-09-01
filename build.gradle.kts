plugins {
    java
    id("org.springframework.boot") version "4.1.1"
}

group = "br.com.fiap"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // BOM do Spring Boot aplicado a todas as configurações relevantes
    // Abordagem nativa do Gradle (platform) em substituição ao io.spring.dependency-management
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

    // Spring Boot Starters (sem versão explícita — gerenciado pelo BOM acima)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Liquibase — starter completo inclui autoconfiguração do Spring Boot 4
    implementation("org.springframework.boot:spring-boot-starter-liquibase")

    // OpenAPI / Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    // MapStruct — mapeamento entre camadas (compile-time, sem reflection)
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Lombok — redução de boilerplate
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Binding Lombok + MapStruct (a ordem dos annotationProcessor importa)
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // Testes
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
