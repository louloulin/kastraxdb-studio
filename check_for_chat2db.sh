#!/bin/bash

# Script to check for any remaining instances of "chat2db" in the codebase

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Checking for remaining instances of chat2db...${NC}"

# Find all text files with "chat2db"
remaining_files=$(find . -type f \
    -not -path "*/\.*" \
    -not -path "*/node_modules/*" \
    -not -path "*/target/*" \
    -not -path "*/build/*" \
    -not -path "./check_for_chat2db.sh" \
    -not -path "./advanced_rename.sh" \
    -not -path "./master_rename.sh" \
    -not -path "./database_migration.sh" \
    -not -path "./verify_rename.sh" \
    -not -path "./rename_chat2db_to_magicdb_mac.sh" \
    -not -path "./rename_chat2db_to_magicdb_complete.sh" \
    -not -path "./fix_java_packages.sh" \
    -not -path "./rename_to_magicdb.sh" \
    -exec grep -l "chat2db" {} \; 2>/dev/null)

if [ -n "$remaining_files" ]; then
    echo -e "${RED}Found remaining instances of chat2db in the following files:${NC}"
    echo "$remaining_files"
    
    echo -e "\n${YELLOW}Details of remaining instances:${NC}"
    for file in $remaining_files; do
        echo -e "${RED}File: $file${NC}"
        grep -n "chat2db" "$file" | head -10  # Show first 10 instances
        echo ""
    done
    
    exit 1
else
    echo -e "${GREEN}No remaining instances of chat2db found in text files!${NC}"
fi

# Check for remaining directories
remaining_dirs=$(find . -type d -name "*chat2db*" \
    -not -path "*/\.*" \
    -not -path "*/node_modules/*" \
    -not -path "*/target/*" \
    -not -path "*/build/*" 2>/dev/null)

if [ -n "$remaining_dirs" ]; then
    echo -e "${RED}Found remaining directories with chat2db in their name:${NC}"
    echo "$remaining_dirs"
    exit 1
else
    echo -e "${GREEN}No remaining directories with chat2db in their name!${NC}"
fi

# Check for remaining files
remaining_named_files=$(find . -type f -name "*chat2db*" \
    -not -path "*/\.*" \
    -not -path "*/node_modules/*" \
    -not -path "*/target/*" \
    -not -path "*/build/*" \
    -not -path "./check_for_chat2db.sh" \
    -not -path "./advanced_rename.sh" \
    -not -path "./master_rename.sh" \
    -not -path "./database_migration.sh" \
    -not -path "./verify_rename.sh" \
    -not -path "./rename_chat2db_to_magicdb_mac.sh" \
    -not -path "./rename_chat2db_to_magicdb_complete.sh" \
    -not -path "./fix_java_packages.sh" \
    -not -path "./rename_to_magicdb.sh" 2>/dev/null)

if [ -n "$remaining_named_files" ]; then
    echo -e "${RED}Found remaining files with chat2db in their name:${NC}"
    echo "$remaining_named_files"
    exit 1
else
    echo -e "${GREEN}No remaining files with chat2db in their name!${NC}"
fi

echo -e "${GREEN}Verification complete! All instances of chat2db have been renamed to magicdb.${NC}"
exit 0
