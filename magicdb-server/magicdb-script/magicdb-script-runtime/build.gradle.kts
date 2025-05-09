/*
 * Build file for magicdb-script-runtime module
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

    // Spring dependencies
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-webmvc")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // Lombok for Java
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.7.0")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.20")
    testImplementation("org.springframework:spring-test")
}

description = "magicdb-script-runtime"

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/java") {
        include("**/*.json")
    }
}
