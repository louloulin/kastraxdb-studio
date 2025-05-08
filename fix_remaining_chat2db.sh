#!/bin/bash

# Script to fix remaining instances of "chat2db" in the codebase

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Fixing remaining instances of chat2db...${NC}"

# Fix META-INF service files
echo -e "${YELLOW}Fixing META-INF service files...${NC}"
find ./magicdb-server/magicdb-plugins -path "*/resources/META-INF/services/*" -type f | while read file; do
    if grep -q "chat2db" "$file"; then
        echo "Updating service file: $file"
        sed -i '' 's/ai\.chat2db/ai\.magicdb/g' "$file"
    fi
done

# Fix client-side references
echo -e "${YELLOW}Fixing client-side references...${NC}"

# Fix data attributes
find ./magicdb-client/src -type f -name "*.tsx" -o -name "*.ts" | while read file; do
    if grep -q "data-chat2db" "$file"; then
        echo "Updating data attributes in: $file"
        sed -i '' 's/data-chat2db/data-magicdb/g' "$file"
    fi
done

# Fix GitHub URLs
find ./magicdb-client/src -type f -name "*.tsx" -o -name "*.ts" | while read file; do
    if grep -q "github.com/chat2db/Chat2DB" "$file"; then
        echo "Updating GitHub URLs in: $file"
        sed -i '' 's|github.com/chat2db/Chat2DB|github.com/magicdb/MagicDB|g' "$file"
    fi
done

# Fix WeChat QR code URLs
find ./magicdb-client/src -type f -name "*.tsx" -o -name "*.ts" | while read file; do
    if grep -q "chat2db_wechat" "$file" || grep -q "chat2db-wechat" "$file"; then
        echo "Updating WeChat QR code URLs in: $file"
        sed -i '' 's/chat2db_wechat/magicdb_wechat/g' "$file"
        sed -i '' 's/chat2db-wechat/magicdb-wechat/g' "$file"
    fi
done

# Fix indexedDB references
find ./magicdb-client/src -type f -name "*.tsx" -o -name "*.ts" | while read file; do
    if grep -q "indexedDB.*chat2db" "$file"; then
        echo "Updating indexedDB references in: $file"
        sed -i '' 's/indexedDB\.deleteData('"'"'chat2db'"'"'/indexedDB.deleteData('"'"'magicdb'"'"'/g' "$file"
    fi
done

# Fix i18n references
find ./magicdb-client/src -type f -name "*.tsx" -o -name "*.ts" | while read file; do
    if grep -q "i18n('.*chat2db" "$file"; then
        echo "Updating i18n references in: $file"
        sed -i '' 's/i18n('"'"'.*chat2db/i18n('"'"'setting.magicdb/g' "$file"
    fi
done

# Fix any other remaining instances
echo -e "${YELLOW}Fixing any other remaining instances...${NC}"
find ./magicdb-client/src -type f -name "*.tsx" -o -name "*.ts" -o -name "*.js" | while read file; do
    if grep -q "chat2db" "$file"; then
        echo "Updating remaining instances in: $file"
        sed -i '' 's/chat2db/magicdb/g' "$file"
        sed -i '' 's/Chat2DB/MagicDB/g' "$file"
    fi
done

echo -e "${GREEN}Fixed remaining instances of chat2db!${NC}"
