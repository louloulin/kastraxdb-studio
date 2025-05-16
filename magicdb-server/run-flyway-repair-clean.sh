#!/bin/bash

# Find the JAR files in the project
FLYWAY_CORE_JAR=$(find magicdb-server -name "flyway-core-*.jar" | head -1)
FLYWAY_MYSQL_JAR=$(find magicdb-server -name "flyway-mysql-*.jar" | head -1)
H2_JAR=$(find magicdb-server -name "h2-*.jar" | head -1)

if [ -z "$FLYWAY_CORE_JAR" ]; then
    echo "Could not find flyway-core JAR in project"
    exit 1
fi

if [ -z "$H2_JAR" ]; then
    echo "Could not find h2 JAR in project"
    exit 1
fi

echo "Using Flyway Core JAR: $FLYWAY_CORE_JAR"
echo "Using Flyway MySQL JAR: $FLYWAY_MYSQL_JAR"
echo "Using H2 JAR: $H2_JAR"

# Compile the Java program
echo "Compiling FlywayRepairWithValidationDisabled.java..."
javac -cp "$FLYWAY_CORE_JAR:$FLYWAY_MYSQL_JAR:$H2_JAR" FlywayRepairWithValidationDisabled.java

# Run the Java program
echo "Running FlywayRepairWithValidationDisabled..."
java -cp ".:$FLYWAY_CORE_JAR:$FLYWAY_MYSQL_JAR:$H2_JAR" FlywayRepairWithValidationDisabled
