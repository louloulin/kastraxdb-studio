#!/bin/bash

# Find the Flyway JAR in the Gradle cache
FLYWAY_CORE_JAR=$(find ~/.gradle -name "flyway-core-9.19.4.jar" | head -1)
FLYWAY_MYSQL_JAR=$(find ~/.gradle -name "flyway-mysql-9.19.4.jar" | head -1)
H2_JAR=$(find ~/.gradle -name "h2-2.1.214.jar" | head -1)

if [ -z "$FLYWAY_CORE_JAR" ]; then
    echo "Could not find flyway-core-9.19.4.jar in Gradle cache"
    exit 1
fi

if [ -z "$H2_JAR" ]; then
    echo "Could not find h2-2.1.214.jar in Gradle cache"
    exit 1
fi

echo "Using Flyway Core JAR: $FLYWAY_CORE_JAR"
echo "Using Flyway MySQL JAR: $FLYWAY_MYSQL_JAR"
echo "Using H2 JAR: $H2_JAR"

# Compile the Java program
echo "Compiling FlywayRepair.java..."
javac -cp "$FLYWAY_CORE_JAR:$FLYWAY_MYSQL_JAR:$H2_JAR" FlywayRepair.java

# Run the Java program
echo "Running FlywayRepair..."
java -cp ".:$FLYWAY_CORE_JAR:$FLYWAY_MYSQL_JAR:$H2_JAR" FlywayRepair
