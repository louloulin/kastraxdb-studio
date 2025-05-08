#!/bin/bash

# Script to verify that all instances of "magicdb" have been renamed to "magicdb"
# This script will search for any remaining instances of "magicdb" in the codebase

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Verifying renaming of magicdb to magicdb...${NC}"

# Function to check for remaining instances of magicdb
check_remaining_instances() {
    echo -e "${GREEN}Checking for remaining instances of magicdb...${NC}"
    
    # Find all text files
    remaining_files=$(find . -type f \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" \
        -not -path "./verify_rename.sh" \
        -not -path "./rename_magicdb_to_magicdb_complete.sh" \
        -not -path "./rename_magicdb_to_magicdb_mac.sh" \
        -not -path "./fix_java_packages.sh" \
        -not -path "./rename_to_magicdb.sh" \
        -exec grep -l "magicdb" {} \; 2>/dev/null)
    
    if [ -n "$remaining_files" ]; then
        echo -e "${RED}Found remaining instances of magicdb in the following files:${NC}"
        echo "$remaining_files"
        
        echo -e "\n${YELLOW}Details of remaining instances:${NC}"
        for file in $remaining_files; do
            echo -e "${RED}File: $file${NC}"
            grep -n "magicdb" "$file" | head -10  # Show first 10 instances
            echo ""
        done
        
        return 1
    else
        echo -e "${GREEN}No remaining instances of magicdb found in text files!${NC}"
    fi
    
    # Check for remaining directories
    remaining_dirs=$(find . -type d -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" 2>/dev/null)
    
    if [ -n "$remaining_dirs" ]; then
        echo -e "${RED}Found remaining directories with magicdb in their name:${NC}"
        echo "$remaining_dirs"
        return 1
    else
        echo -e "${GREEN}No remaining directories with magicdb in their name!${NC}"
    fi
    
    # Check for remaining files
    remaining_named_files=$(find . -type f -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "./verify_rename.sh" \
        -not -path "./rename_magicdb_to_magicdb_complete.sh" \
        -not -path "./rename_magicdb_to_magicdb_mac.sh" \
        -not -path "./fix_java_packages.sh" \
        -not -path "./rename_to_magicdb.sh" 2>/dev/null)
    
    if [ -n "$remaining_named_files" ]; then
        echo -e "${RED}Found remaining files with magicdb in their name:${NC}"
        echo "$remaining_named_files"
        return 1
    else
        echo -e "${GREEN}No remaining files with magicdb in their name!${NC}"
    fi
    
    return 0
}

# Main execution
if check_remaining_instances; then
    echo -e "${GREEN}Verification complete! All instances of magicdb have been renamed to magicdb.${NC}"
    exit 0
else
    echo -e "${RED}Verification failed! Some instances of magicdb remain in the codebase.${NC}"
    echo -e "${YELLOW}Please review the output above and fix the remaining instances manually.${NC}"
    exit 1
fi
