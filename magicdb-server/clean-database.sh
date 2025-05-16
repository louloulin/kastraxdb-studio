#!/bin/bash

# Find the H2 JAR in the Gradle cache
H2_JAR=$(find ~/.gradle -name "h2-2.1.214.jar" | head -1)

if [ -z "$H2_JAR" ]; then
    echo "Could not find H2 JAR in Gradle cache"
    exit 1
fi

echo "Using H2 JAR: $H2_JAR"

# Create a SQL script to drop all tables
cat > drop-all-tables.sql << EOF
-- Drop all tables in the database
DROP ALL OBJECTS;
EOF

# Run the SQL script against the H2 database
echo "Running SQL script to drop all tables..."
java -cp "$H2_JAR" org.h2.tools.RunScript -url "jdbc:h2:~/.magicdb/db/magicdb_dev;FILE_LOCK=NO;MODE=MYSQL" -user "" -password "" -script "drop-all-tables.sql" -showResults

echo "Database cleaned successfully"
