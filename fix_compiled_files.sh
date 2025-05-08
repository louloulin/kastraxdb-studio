#!/bin/bash

# Script to fix remaining instances of "chat2db" in compiled files and Docker files

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Fixing remaining instances of chat2db in compiled files and Docker files...${NC}"

# Fix Docker files
echo -e "${YELLOW}Fixing Docker files...${NC}"
if [ -f "./docker/Dockerfile" ]; then
    echo "Updating Docker file"
    sed -i '' 's/chat2db/magicdb/g' "./docker/Dockerfile"
    sed -i '' 's/Chat2DB/MagicDB/g' "./docker/Dockerfile"
fi

# Fix compiled JavaScript files in client/dist
echo -e "${YELLOW}Fixing compiled JavaScript files...${NC}"
find ./magicdb-client/dist -type f -name "*.js" | while read file; do
    if grep -q "chat2db" "$file"; then
        echo "Updating compiled JavaScript file: $file"
        sed -i '' 's/chat2db/magicdb/g' "$file"
        sed -i '' 's/Chat2DB/MagicDB/g' "$file"
    fi
done

# Fix compiled JavaScript files in client/release
echo -e "${YELLOW}Fixing compiled JavaScript files in release directory...${NC}"
find ./magicdb-client/release -type f -name "*.js" | while read file; do
    if grep -q "chat2db" "$file"; then
        echo "Updating compiled JavaScript file: $file"
        sed -i '' 's/chat2db/magicdb/g' "$file"
        sed -i '' 's/Chat2DB/MagicDB/g' "$file"
    fi
done

# Fix GitHub URLs in compiled files
echo -e "${YELLOW}Fixing GitHub URLs in compiled files...${NC}"
find ./magicdb-client/dist ./magicdb-client/release -type f -name "*.js" | while read file; do
    if grep -q "github.com/chat2db/Chat2DB" "$file"; then
        echo "Updating GitHub URLs in: $file"
        sed -i '' 's|github.com/chat2db/Chat2DB|github.com/magicdb/MagicDB|g' "$file"
    fi
done

# Fix WeChat QR code URLs in compiled files
echo -e "${YELLOW}Fixing WeChat QR code URLs in compiled files...${NC}"
find ./magicdb-client/dist ./magicdb-client/release -type f -name "*.js" | while read file; do
    if grep -q "chat2db_wechat" "$file" || grep -q "chat2db-wechat" "$file"; then
        echo "Updating WeChat QR code URLs in: $file"
        sed -i '' 's/chat2db_wechat/magicdb_wechat/g' "$file"
        sed -i '' 's/chat2db-wechat/magicdb-wechat/g' "$file"
    fi
done

echo -e "${GREEN}Fixed remaining instances of chat2db in compiled files and Docker files!${NC}"
