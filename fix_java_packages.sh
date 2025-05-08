#!/bin/bash

# Script to fix Java/Kotlin package structure after renaming
# This script focuses on ensuring all package declarations and imports are correctly updated

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Java/Kotlin package structure fix...${NC}"

# Function to update Java/Kotlin package declarations
fix_package_declarations() {
    echo -e "${GREEN}Fixing package declarations in Java/Kotlin files...${NC}"
    
    # Find all Java and Kotlin files
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | while read file; do
        
        # Check if file contains package declaration with magicdb or if it should contain magicdb
        if grep -q "package.*magicdb" "$file" || grep -q "package.*magicdb" "$file"; then
            echo "Checking package in: $file"
            
            # Get the directory structure
            rel_path=$(echo "$file" | sed 's/\.\///')
            dir_path=$(dirname "$rel_path")
            
            # Extract expected package from directory structure
            # Remove src/main/java or src/main/kotlin prefix if present
            package_path=$(echo "$dir_path" | sed -E 's/^(.*\/)?src\/main\/(java|kotlin)\///')
            
            # Convert directory separators to package dots
            expected_package=$(echo "$package_path" | sed 's/\//./g')
            
            # Check if the file has the correct package declaration
            if ! grep -q "package $expected_package" "$file"; then
                echo -e "${YELLOW}Fixing incorrect package in: $file${NC}"
                echo -e "${YELLOW}Expected package: $expected_package${NC}"
                
                # Get the current package declaration
                current_package=$(grep "package" "$file" | head -1 | sed 's/package //' | sed 's/;//')
                
                if [ -n "$current_package" ]; then
                    # Replace the package declaration
                    sed -i '' "s/package $current_package/package $expected_package/" "$file"
                else
                    # Add package declaration if missing
                    sed -i '' "1i\\
package $expected_package;
" "$file"
                fi
            fi
        fi
    done
}

# Function to update import statements
fix_import_statements() {
    echo -e "${GREEN}Fixing import statements in Java/Kotlin files...${NC}"
    
    # Find all Java and Kotlin files
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" | while read file; do
        
        # Check if file contains import statements with magicdb
        if grep -q "import.*magicdb" "$file"; then
            echo "Fixing imports in: $file"
            sed -i '' 's/import \([[:alnum:]\.]*\)magicdb/import \1magicdb/g' "$file"
        fi
    done
}

# Main execution
echo -e "${GREEN}Step 1: Fixing package declarations${NC}"
fix_package_declarations

echo -e "${GREEN}Step 2: Fixing import statements${NC}"
fix_import_statements

echo -e "${YELLOW}Java/Kotlin package structure fix complete!${NC}"
echo -e "${RED}Note: You may need to manually update some references that the script couldn't handle.${NC}"
