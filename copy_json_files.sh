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
    # Create resources directory
    mkdir -p "magicdb-server/magicdb-plugins/magicdb-$plugin/src/main/resources/ai/magicdb/plugin/$plugin"
    
    # Find the JSON file
    json_file=$(find "magicdb-server/magicdb-plugins/magicdb-$plugin/src/main/java/ai/magicdb/plugin/$plugin" -name "*.json" | head -1)
    
    if [ -n "$json_file" ]; then
        # Copy the JSON file to resources directory
        cp "$json_file" "magicdb-server/magicdb-plugins/magicdb-$plugin/src/main/resources/ai/magicdb/plugin/$plugin/"
        echo "Copied $json_file to magicdb-server/magicdb-plugins/magicdb-$plugin/src/main/resources/ai/magicdb/plugin/$plugin/"
    else
        echo "No JSON file found for plugin $plugin"
    fi
done
