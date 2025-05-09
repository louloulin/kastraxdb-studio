/*
 * Build file for magicdb-script module
 */

plugins {
    id("buildlogic.java-conventions")
}

description = "magicdb-script"

// Common configuration for all script subprojects
subprojects {
    apply {
        plugin("buildlogic.java-conventions")
    }

    // Apply Kotlin plugin to all script projects
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
    }

    dependencies {
        // Common dependency for all script modules
        implementation(project(":magicdb-spi"))
    }

    // Configure resources to include JSON files from src/main/java and src/main/kotlin
    tasks.withType<ProcessResources> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from("src/main/java") {
            include("**/*.json")
        }
        from("src/main/kotlin") {
            include("**/*.json")
        }
    }

    // Configure Kotlin compiler options
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}
