/*
 * Build file for magicdb-data-service-core module
 */

plugins {
    id("buildlogic.java-conventions")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":magicdb-spi"))
    implementation(project(":magicdb-script-api"))
    implementation(project(":magicdb-script-engine"))
    implementation(project(":magicdb-data-service-api"))
    implementation(project(":magicdb-server-domain-repository"))
    implementation(project(":magicdb-server-domain-core"))
    implementation(project(":magicdb-server-tools-common"))

    // Spring dependencies
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")

    // MyBatis-Plus
    implementation("com.baomidou:mybatis-plus:3.5.3")

    // Database
    implementation("org.flywaydb:flyway-core")
    implementation("com.h2database:h2")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Lombok for Java
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

description = "magicdb-data-service-core"

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/java") {
        include("**/*.json")
    }
}
