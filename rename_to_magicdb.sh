#!/bin/bash

# Script to rename magicdb to magicdb
# This script will:
# 1. Replace occurrences of "magicdb" with "magicdb" in file contents
# 2. Rename files with "magicdb" in their name
# 3. Rename directories with "magicdb" in their name

set -e  # Exit on error

echo "Starting to rename magicdb to magicdb..."

# Function to replace content in files
replace_content() {
    echo "Replacing content in files..."
    
    # Find all text files (excluding .git directory, node_modules, and this script)
    find . -type f \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "./rename_to_magicdb.sh" \
        -not -path "./rename_magicdb_to_magicdb.sh" | while read file; do
        
        # Skip binary files
        if file "$file" | grep -q "binary"; then
            continue
        fi
        
        # Check if file contains any form of "magicdb"
        if grep -i "magicdb" "$file" 2>/dev/null; then
            echo "Updating content in: $file"
            
            # Create a temporary file
            temp_file="${file}.tmp"
            
            # Replace with case preservation
            # 1. Replace exact matches with different cases
            # 2. Replace in package names, URLs, etc.
            cat "$file" | \
                sed 's/magicdb/magicdb/g' | \
                sed 's/MagicDB/MagicDB/g' | \
                sed 's/Magicdb/Magicdb/g' | \
                sed 's/MAGICDB/MAGICDB/g' | \
                sed 's/ai\.magicdb/ai\.magicdb/g' | \
                sed 's/\.magicdb\./\.magicdb\./g' | \
                sed 's/\/magicdb\//\/magicdb\//g' > "$temp_file"
            
            # Replace the original file
            mv "$temp_file" "$file"
        fi
    done
}

# Function to rename files
rename_files() {
    echo "Renaming files..."
    
    # Find all files with "magicdb" in their name
    find . -type f -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | while read file; do
        
        dir_name=$(dirname "$file")
        base_name=$(basename "$file")
        new_base_name=$(echo "$base_name" | sed 's/magicdb/magicdb/g; s/MagicDB/MagicDB/g; s/Magicdb/Magicdb/g')
        new_name="${dir_name}/${new_base_name}"
        
        if [ "$file" != "$new_name" ]; then
            echo "Renaming file: $file -> $new_name"
            mv "$file" "$new_name"
        fi
    done
}

# Function to rename directories (bottom-up to avoid path issues)
rename_directories() {
    echo "Renaming directories..."
    
    # Find all directories with "magicdb" in their name, sort in reverse order (deepest first)
    find . -type d -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | sort -r | while read dir; do
        
        parent_dir=$(dirname "$dir")
        base_dir=$(basename "$dir")
        new_base_dir=$(echo "$base_dir" | sed 's/magicdb/magicdb/g; s/MagicDB/MagicDB/g; s/Magicdb/Magicdb/g')
        new_dir="${parent_dir}/${new_base_dir}"
        
        if [ "$dir" != "$new_dir" ]; then
            echo "Renaming directory: $dir -> $new_dir"
            
            # Check if the directory still exists (might have been moved as part of a parent move)
            if [ -d "$dir" ]; then
                # Create the new directory
                mkdir -p "$new_dir"
                
                # Move all contents
                mv "$dir"/* "$new_dir" 2>/dev/null || true
                mv "$dir"/.[!.]* "$new_dir" 2>/dev/null || true
                
                # Remove the old directory
                rmdir "$dir" 2>/dev/null || true
            fi
        fi
    done
}

# Function to update Java/Kotlin package declarations
update_packages() {
    echo "Updating package declarations in Java/Kotlin files..."
    
    # Find all Java and Kotlin files
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | while read file; do
        
        # Check if file contains package declaration with magicdb
        if grep -q "package.*magicdb" "$file"; then
            echo "Updating package in: $file"
            
            # Create a temporary file
            temp_file="${file}.tmp"
            
            # Replace package declaration
            sed 's/package \(.*\)magicdb/package \1magicdb/g' "$file" > "$temp_file"
            
            # Replace the original file
            mv "$temp_file" "$file"
        fi
    done
}

# Function to update import statements
update_imports() {
    echo "Updating import statements in Java/Kotlin files..."
    
    # Find all Java and Kotlin files
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | while read file; do
        
        # Check if file contains import statements with magicdb
        if grep -q "import.*magicdb" "$file"; then
            echo "Updating imports in: $file"
            
            # Create a temporary file
            temp_file="${file}.tmp"
            
            # Replace import statements
            sed 's/import \(.*\)magicdb/import \1magicdb/g' "$file" > "$temp_file"
            
            # Replace the original file
            mv "$temp_file" "$file"
        fi
    done
}

# Function to update Maven POM files
update_pom_files() {
    echo "Updating Maven POM files..."
    
    # Find all pom.xml files
    find . -name "pom.xml" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | while read file; do
        
        # Check if file contains magicdb
        if grep -q "magicdb" "$file"; then
            echo "Updating POM file: $file"
            
            # Create a temporary file
            temp_file="${file}.tmp"
            
            # Replace groupId, artifactId, and dependencies
            cat "$file" | \
                sed 's/<groupId>ai\.magicdb<\/groupId>/<groupId>ai\.magicdb<\/groupId>/g' | \
                sed 's/<artifactId>magicdb/<artifactId>magicdb/g' | \
                sed 's/<n>magicdb/<n>magicdb/g' | \
                sed 's/ai\.magicdb/ai\.magicdb/g' > "$temp_file"
            
            # Replace the original file
            mv "$temp_file" "$file"
        fi
    done
}

# Main execution
echo "Step 1: Replacing content in files"
replace_content

echo "Step 2: Updating Java/Kotlin package declarations"
update_packages

echo "Step 3: Updating import statements"
update_imports

echo "Step 4: Updating Maven POM files"
update_pom_files

echo "Step 5: Renaming files"
rename_files

echo "Step 6: Renaming directories"
rename_directories

echo "Renaming complete! Please review the changes."
echo "Note: You may need to manually update some references that the script couldn't handle."
