#!/bin/bash

# Get the H2 database path from application-dev.yml
DB_PATH=~/.magicdb/db/magicdb_dev

echo "Running Flyway repair on database: $DB_PATH"

# Run Flyway repair command
java -cp "./magicdb-server-start/build/libs/magicdb-server-start-1.0.0.jar" \
  org.flywaydb.core.Flyway \
  -url="jdbc:h2:$DB_PATH;FILE_LOCK=NO;MODE=MYSQL" \
  -user="" \
  -password="" \
  repair

echo "Flyway repair completed"
