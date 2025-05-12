#!/bin/bash

# 设置源包
SOURCE_PACKAGE="ai/magicdb/dataservice"

# 设置基础目录
BASE_DIR="magicdb-server/magicdb-data-service"

# 查找并删除 ai.magicdb.dataservice 包下的所有文件
find "$BASE_DIR" -type f -path "*/$SOURCE_PACKAGE/*" | while read -r file; do
  echo "删除文件: $file"
  rm -f "$file"
done

# 删除空目录
find "$BASE_DIR" -type d -path "*/$SOURCE_PACKAGE/*" -empty -delete

# 删除 ai/magicdb/dataservice 目录（如果为空）
find "$BASE_DIR" -type d -path "*/$SOURCE_PACKAGE" -empty -delete

# 删除 ai/magicdb 目录（如果为空）
find "$BASE_DIR" -type d -path "*/ai/magicdb" -empty -delete

# 删除 ai 目录（如果为空）
find "$BASE_DIR" -type d -path "*/ai" -empty -delete

echo "删除完成！"
