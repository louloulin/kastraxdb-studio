#!/bin/bash

# Script to handle database migration aspects of renaming magicdb to magicdb
# This script will:
# 1. Identify database paths and configuration
# 2. Create a backup of the database
# 3. Update database paths and configuration

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Timestamp for logs and backups
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
LOG_FILE="db_migration_log_${TIMESTAMP}.txt"

# Function to log messages
log_message() {
    echo -e "$1" | tee -a "$LOG_FILE"
}

# Function to identify database paths
identify_db_paths() {
    log_message "${BLUE}${BOLD}Identifying database paths...${NC}"
    
    # Look for database configuration in application.yml files
    db_config_files=$(find . -name "application*.yml" -o -name "application*.properties" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*")
    
    log_message "${YELLOW}Found the following database configuration files:${NC}"
    for file in $db_config_files; do
        log_message "- $file"
        
        # Extract database paths
        if grep -q "magicdb" "$file"; then
            log_message "${YELLOW}Database configuration in $file:${NC}"
            grep -A 5 "magicdb" "$file" | grep -v "^--$"
        fi
    done
    
    # Look for database paths in Java/Kotlin files
    db_code_files=$(find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -exec grep -l "\.magicdb" {} \;)
    
    if [ -n "$db_code_files" ]; then
        log_message "${YELLOW}Found database references in the following code files:${NC}"
        for file in $db_code_files; do
            log_message "- $file"
            grep -n "\.magicdb" "$file" | head -5  # Show first 5 instances
        done
    fi
    
    # Default database path for magicdb
    DEFAULT_DB_PATH="~/.magicdb/db"
    log_message "${YELLOW}Default database path: ${DEFAULT_DB_PATH}${NC}"
    
    # Check if the default database path exists
    if [ -d ~/.magicdb/db ]; then
        log_message "${GREEN}Default database directory exists at: ~/.magicdb/db${NC}"
    else
        log_message "${RED}Default database directory not found at: ~/.magicdb/db${NC}"
    fi
    
    return 0
}

# Function to backup database
backup_database() {
    log_message "${BLUE}${BOLD}Creating database backup...${NC}"
    
    # Create backup directory
    DB_BACKUP_DIR="$HOME/magicdb_db_backup_${TIMESTAMP}"
    mkdir -p "$DB_BACKUP_DIR"
    
    # Check if the default database directory exists
    if [ -d ~/.magicdb/db ]; then
        log_message "${YELLOW}Backing up database from ~/.magicdb/db to ${DB_BACKUP_DIR}${NC}"
        cp -R ~/.magicdb/db/* "$DB_BACKUP_DIR/" 2>/dev/null || true
        log_message "${GREEN}Database backup created at: ${DB_BACKUP_DIR}${NC}"
    else
        log_message "${RED}Default database directory not found at: ~/.magicdb/db${NC}"
        log_message "${YELLOW}Skipping database backup.${NC}"
    fi
    
    return 0
}

# Function to update database paths
update_database_paths() {
    log_message "${BLUE}${BOLD}Updating database paths...${NC}"
    
    # Create new database directory
    mkdir -p ~/.magicdb/db
    
    # Check if the default database directory exists
    if [ -d ~/.magicdb/db ]; then
        log_message "${YELLOW}Copying database from ~/.magicdb/db to ~/.magicdb/db${NC}"
        cp -R ~/.magicdb/db/* ~/.magicdb/db/ 2>/dev/null || true
        log_message "${GREEN}Database copied to new location.${NC}"
    else
        log_message "${RED}Default database directory not found at: ~/.magicdb/db${NC}"
        log_message "${YELLOW}No database to migrate.${NC}"
    fi
    
    # Update database paths in configuration files
    log_message "${YELLOW}Updating database paths in configuration files...${NC}"
    
    # Find all configuration files
    find . \( -name "*.yml" -o -name "*.yaml" -o -name "*.properties" -o -name "*.json" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Check if file contains database path references
        if grep -q "\.magicdb" "$file"; then
            log_message "Updating database paths in: $file"
            
            # Replace database paths
            sed -i '' 's/\.magicdb\//\.magicdb\//g' "$file"
            sed -i '' 's/\.magicdb/\.magicdb/g' "$file"
        fi
    done
    
    return 0
}

# Function to provide guidance for database migration
provide_db_guidance() {
    log_message "\n${BLUE}${BOLD}Database Migration Guidance:${NC}"
    log_message "${YELLOW}1. Database files:${NC}"
    log_message "   - Original database: ~/.magicdb/db/"
    log_message "   - New database: ~/.magicdb/db/"
    log_message "   - Backup: ${DB_BACKUP_DIR}"
    
    log_message "\n${YELLOW}2. First run after migration:${NC}"
    log_message "   - When you first run the application after renaming, verify that your data is intact"
    log_message "   - If there are issues, you can restore from the backup"
    
    log_message "\n${YELLOW}3. Restoring from backup (if needed):${NC}"
    log_message "   - Stop the application"
    log_message "   - Delete ~/.magicdb/db/*"
    log_message "   - Copy files from ${DB_BACKUP_DIR} to ~/.magicdb/db/"
    log_message "   - Restart the application"
    
    log_message "\n${GREEN}${BOLD}Database migration log has been saved to: ${LOG_FILE}${NC}"
}

# Main execution
log_message "${BLUE}${BOLD}=== Starting Database Migration for magicdb to magicdb Renaming ===${NC}"
log_message "Timestamp: $(date)"

if ! identify_db_paths; then
    log_message "${RED}${BOLD}Failed to identify database paths. Proceeding with caution.${NC}"
fi

if ! backup_database; then
    log_message "${RED}${BOLD}Database backup failed. Proceeding with caution.${NC}"
fi

if ! update_database_paths; then
    log_message "${RED}${BOLD}Database path update failed. Please check the log for details.${NC}"
    exit 1
fi

provide_db_guidance
log_message "${GREEN}${BOLD}Database migration completed!${NC}"
exit 0
