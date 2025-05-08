#!/bin/bash

# List of plugins
plugins=(
    "clickhouse"
    "db2"
    "dm"
    "h2"
    "hive"
    "kingbase"
    "mariadb"
    "mongodb"
    "mysql"
    "oceanbase"
    "oracle"
    "postgresql"
    "presto"
    "sqlite"
    "sqlserver"
    "timeplus"
)

# Process each plugin
for plugin in "${plugins[@]}"; do
    build_file="magicdb-server/magicdb-plugins/magicdb-$plugin/build.gradle.kts"
    
    # Check if the build file exists
    if [ -f "$build_file" ]; then
        # Update the build file
        sed -i '' 's/tasks.withType<ProcessResources> {/tasks.withType<ProcessResources> {\n    duplicatesStrategy = DuplicatesStrategy.EXCLUDE/' "$build_file"
        echo "Updated $build_file"
    else
        echo "Build file not found: $build_file"
    fi
done
