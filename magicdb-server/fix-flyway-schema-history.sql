-- SQL script to fix the Flyway schema history table
-- This will mark the missing migrations as deleted and update the checksum for V2.1.6

-- Connect to the H2 database
-- JDBC URL: jdbc:h2:~/.magicdb/db/magicdb_dev;FILE_LOCK=NO;MODE=MYSQL

-- Mark missing migrations as deleted
UPDATE flyway_schema_history 
SET success = 0, description = 'Marked as deleted by repair script' 
WHERE version = '1.0.1' OR version = '2.0.0';

-- Update the checksum for V2.1.6
UPDATE flyway_schema_history 
SET checksum = 13011577 
WHERE version = '2.1.6';
