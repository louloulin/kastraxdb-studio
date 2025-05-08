/*
 * Build file for magicdb-clickhouse module
 * This module will be converted to Kotlin
 */

plugins {
    id("buildlogic.java-conventions")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":magicdb-spi"))
}

description = "magicdb-clickhouse"

// Configure resources to include JSON files from src/main/java
tasks.withType<ProcessResources> {
    from("src/main/java") {
        include("**/*.json")
    }
}
