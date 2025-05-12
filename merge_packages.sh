#!/bin/bash

# 设置源包和目标包
SOURCE_PACKAGE="ai/magicdb/dataservice"
TARGET_PACKAGE="ai/magicdb/data/service"

# 设置基础目录
BASE_DIR="magicdb-server/magicdb-data-service"

# 创建目标目录（如果不存在）
find "$BASE_DIR" -type d -path "*/$SOURCE_PACKAGE*" | while read -r src_dir; do
  # 替换路径中的包名
  target_dir="${src_dir/$SOURCE_PACKAGE/$TARGET_PACKAGE}"
  
  # 创建目标目录
  mkdir -p "$target_dir"
  echo "创建目录: $target_dir"
done

# 复制文件到目标目录
find "$BASE_DIR" -type f -path "*/$SOURCE_PACKAGE/*" | while read -r src_file; do
  # 替换路径中的包名
  target_file="${src_file/$SOURCE_PACKAGE/$TARGET_PACKAGE}"
  
  # 检查目标文件是否已存在
  if [ -f "$target_file" ]; then
    echo "目标文件已存在，跳过: $target_file"
    continue
  fi
  
  # 创建目标目录（如果不存在）
  target_dir=$(dirname "$target_file")
  mkdir -p "$target_dir"
  
  # 复制文件
  cp "$src_file" "$target_file"
  
  # 修改文件中的包名
  sed -i '' "s/package ai.magicdb.dataservice/package ai.magicdb.data.service/g" "$target_file"
  sed -i '' "s/import ai.magicdb.dataservice/import ai.magicdb.data.service/g" "$target_file"
  
  echo "复制并修改文件: $target_file"
done

echo "合并完成！"
