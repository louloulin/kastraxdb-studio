/*
 * Build file for magicdb-data-service-web module
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
    implementation(project(":magicdb-data-service-core"))

    // Spring dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-context")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // Lombok for Java
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

description = "magicdb-data-service-web"

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/java") {
        include("**/*.json")
    }
}
