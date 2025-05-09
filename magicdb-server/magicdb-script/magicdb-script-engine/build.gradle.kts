/*
 * Build file for magicdb-script-engine module
 */

plugins {
    id("buildlogic.java-conventions")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":magicdb-spi"))
    implementation(project(":magicdb-script-api"))

    // Script engine dependencies
    implementation("org.openjdk.nashorn:nashorn-core:15.4")


    // Lombok for Java
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

description = "magicdb-script-engine"

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/java") {
        include("**/*.json")
    }
}
