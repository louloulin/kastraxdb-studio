#!/bin/bash

# Script to perform final verification of the renaming process
# This script excludes our script files and log files

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Performing final verification of the renaming process...${NC}"

# Check for remaining instances of chat2db in source code files
echo -e "${YELLOW}Checking source code files...${NC}"
remaining_files=$(find ./magicdb-server ./magicdb-client/src -type f \
    -not -path "*/\.*" \
    -not -path "*/node_modules/*" \
    -not -path "*/target/*" \
    -not -path "*/build/*" \
    -not -path "*/dist/*" \
    -not -path "*/release/*" \
    -exec grep -l "chat2db" {} \; 2>/dev/null)

if [ -n "$remaining_files" ]; then
    echo -e "${RED}Found remaining instances of chat2db in source code files:${NC}"
    echo "$remaining_files"
    
    echo -e "\n${YELLOW}Details of remaining instances:${NC}"
    for file in $remaining_files; do
        echo -e "${RED}File: $file${NC}"
        grep -n "chat2db" "$file" | head -10  # Show first 10 instances
        echo ""
    done
    
    exit 1
else
    echo -e "${GREEN}No remaining instances of chat2db found in source code files!${NC}"
fi

# Check for remaining directories with chat2db in their name
echo -e "${YELLOW}Checking for directories with chat2db in their name...${NC}"
remaining_dirs=$(find ./magicdb-server ./magicdb-client -type d -name "*chat2db*" \
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

echo -e "${GREEN}Verification complete! The renaming process was successful.${NC}"
echo -e "${YELLOW}Note: Script files and log files may still contain references to chat2db, but these don't affect the actual codebase.${NC}"
exit 0
