#!/bin/bash

# Find all plugin directories
find magicdb-server/magicdb-plugins -type d -name "src/main/java/ai/magicdb/plugin/*" | while read plugin_dir; do
    # Extract plugin name
    plugin_name=$(basename "$plugin_dir")
    
    # Create resources directory if it doesn't exist
    resources_dir="${plugin_dir/java/resources}"
    mkdir -p "$resources_dir"
    
    # Find JSON files in the plugin directory
    find "$plugin_dir" -name "*.json" | while read json_file; do
        # Extract JSON file name
        json_name=$(basename "$json_file")
        
        # Copy JSON file to resources directory
        cp "$json_file" "$resources_dir/$json_name"
        echo "Copied $json_file to $resources_dir/$json_name"
    done
done
