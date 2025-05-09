/*
 * Build file for magicdb-data-service module
 */

plugins {
    id("buildlogic.java-conventions")
}

description = "magicdb-data-service"

// Common configuration for all data service subprojects
subprojects {
    apply {
        plugin("buildlogic.java-conventions")
    }

    // Apply Kotlin plugin to all data service projects
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
    }

    dependencies {
        // Common dependency for all data service modules
        implementation(project(":magicdb-spi"))
        implementation(project(":magicdb-script-api"))
    }

    // Configure resources to include JSON files from src/main/java
    tasks.withType<ProcessResources> {
        from("src/main/java") {
            include("**/*.json")
        }
    }
}
