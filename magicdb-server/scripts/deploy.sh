#!/bin/bash

# 部署脚本
# 用于自动化部署应用程序到不同环境
# 使用方法: ./deploy.sh [环境] [版本]
# 例如: ./deploy.sh dev 1.0.0

# 获取参数
ENV=$1
VERSION=$2

# 检查参数
if [ -z "$ENV" ]; then
  echo "错误: 未指定环境"
  echo "使用方法: ./deploy.sh [环境] [版本]"
  echo "例如: ./deploy.sh dev 1.0.0"
  exit 1
fi

if [ -z "$VERSION" ]; then
  echo "错误: 未指定版本"
  echo "使用方法: ./deploy.sh [环境] [版本]"
  echo "例如: ./deploy.sh dev 1.0.0"
  exit 1
fi

# 设置环境变量
case $ENV in
  dev)
    SERVER_HOST="dev-server.magicdb.ai"
    SERVER_PORT=22
    SERVER_USER="deploy"
    SERVER_PATH="/opt/magicdb/dev"
    CONFIG_FILE="application-dev.yml"
    ;;
  test)
    SERVER_HOST="test-server.magicdb.ai"
    SERVER_PORT=22
    SERVER_USER="deploy"
    SERVER_PATH="/opt/magicdb/test"
    CONFIG_FILE="application-test.yml"
    ;;
  staging)
    SERVER_HOST="staging-server.magicdb.ai"
    SERVER_PORT=22
    SERVER_USER="deploy"
    SERVER_PATH="/opt/magicdb/staging"
    CONFIG_FILE="application-staging.yml"
    ;;
  prod)
    SERVER_HOST="prod-server.magicdb.ai"
    SERVER_PORT=22
    SERVER_USER="deploy"
    SERVER_PATH="/opt/magicdb/prod"
    CONFIG_FILE="application-prod.yml"
    ;;
  *)
    echo "错误: 未知环境 $ENV"
    echo "支持的环境: dev, test, staging, prod"
    exit 1
    ;;
esac

# 构建应用程序
echo "构建应用程序..."
./gradlew clean build -x test

# 检查构建结果
if [ $? -ne 0 ]; then
  echo "错误: 构建失败"
  exit 1
fi

# 准备部署文件
echo "准备部署文件..."
DEPLOY_DIR="build/deploy"
mkdir -p $DEPLOY_DIR

# 复制JAR文件
cp magicdb-server-web-start/build/libs/magicdb-server-web-start-*.jar $DEPLOY_DIR/magicdb-server.jar

# 复制配置文件
cp magicdb-server-web-start/src/main/resources/$CONFIG_FILE $DEPLOY_DIR/application.yml

# 创建部署脚本
cat > $DEPLOY_DIR/start.sh << EOF
#!/bin/bash
java -jar magicdb-server.jar --spring.config.location=file:./application.yml
EOF

chmod +x $DEPLOY_DIR/start.sh

# 创建停止脚本
cat > $DEPLOY_DIR/stop.sh << EOF
#!/bin/bash
PID=\$(ps -ef | grep magicdb-server.jar | grep -v grep | awk '{print \$2}')
if [ -z "\$PID" ]; then
  echo "应用程序未运行"
else
  echo "停止应用程序 (PID: \$PID)..."
  kill \$PID
  sleep 5
  PID=\$(ps -ef | grep magicdb-server.jar | grep -v grep | awk '{print \$2}')
  if [ -z "\$PID" ]; then
    echo "应用程序已停止"
  else
    echo "强制停止应用程序..."
    kill -9 \$PID
  fi
fi
EOF

chmod +x $DEPLOY_DIR/stop.sh

# 创建部署包
echo "创建部署包..."
cd build
tar -czf magicdb-server-$VERSION.tar.gz deploy
cd ..

# 部署到服务器
echo "部署到服务器 $SERVER_HOST..."
scp -P $SERVER_PORT build/magicdb-server-$VERSION.tar.gz $SERVER_USER@$SERVER_HOST:$SERVER_PATH/

# 在服务器上解压部署包
ssh -p $SERVER_PORT $SERVER_USER@$SERVER_HOST << EOF
cd $SERVER_PATH
tar -xzf magicdb-server-$VERSION.tar.gz
cd deploy
./stop.sh
./start.sh
EOF

echo "部署完成"
