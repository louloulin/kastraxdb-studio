#!/bin/bash

# Find the H2 JAR in the project
H2_JAR=$(find magicdb-server -name "h2-*.jar" | head -1)

if [ -z "$H2_JAR" ]; then
    echo "Could not find H2 JAR in project, trying in Gradle cache"
    H2_JAR=$(find ~/.gradle -name "h2-2.1.214.jar" | head -1)
    
    if [ -z "$H2_JAR" ]; then
        echo "Could not find H2 JAR in Gradle cache either"
        exit 1
    fi
fi

echo "Using H2 JAR: $H2_JAR"

# Run the SQL script against the H2 database
echo "Running SQL script to fix Flyway schema history table..."
java -cp "$H2_JAR" org.h2.tools.RunScript -url "jdbc:h2:~/.magicdb/db/magicdb_dev;FILE_LOCK=NO;MODE=MYSQL" -user "" -password "" -script "fix-flyway-schema-history.sql" -showResults
