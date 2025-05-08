/*
 * Build file for magicdb-plugins module
 */

plugins {
    id("buildlogic.java-conventions")
}

description = "magicdb-plugins"

// Common configuration for all plugin subprojects
subprojects {
    apply {
        plugin("buildlogic.java-conventions")
    }

    // Apply Kotlin plugin to projects that need it
    if (project.name == "magicdb-clickhouse") {
        apply {
            plugin("org.jetbrains.kotlin.jvm")
            plugin("org.jetbrains.kotlin.plugin.spring")
        }
    }

    dependencies {
        // Common dependency for all plugins
        implementation(project(":magicdb-spi"))
    }

    // Special dependencies for specific plugins
    if (project.name == "magicdb-mariadb" || project.name == "magicdb-oceanbase") {
        dependencies {
            implementation(project(":magicdb-mysql"))
        }
    }

    // Configure resources to include JSON files from src/main/java
    tasks.withType<ProcessResources> {
        from("src/main/java") {
            include("**/*.json")
        }
    }
}
