#!/bin/bash

# Advanced script for renaming chat2db to magicdb
# This script handles complex edge cases and provides detailed reporting

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
LOG_FILE="advanced_rename_log_${TIMESTAMP}.txt"
REPORT_FILE="rename_report_${TIMESTAMP}.txt"

# Function to log messages
log_message() {
    echo -e "$1" | tee -a "$LOG_FILE"
}

# Function to add to report
add_to_report() {
    echo -e "$1" >> "$REPORT_FILE"
}

# Initialize report
initialize_report() {
    echo -e "# Chat2DB to MagicDB Renaming Report" > "$REPORT_FILE"
    echo -e "Generated: $(date)\n" >> "$REPORT_FILE"
    echo -e "## Summary\n" >> "$REPORT_FILE"
    echo -e "## Files Modified\n" >> "$REPORT_FILE"
    echo -e "## Directories Renamed\n" >> "$REPORT_FILE"
    echo -e "## Potential Issues\n" >> "$REPORT_FILE"
}

# Function to create backup
create_backup() {
    log_message "${BLUE}${BOLD}Creating backup of the project...${NC}"
    
    BACKUP_DIR="../chat2db_backup_${TIMESTAMP}"
    log_message "${YELLOW}Creating backup at: ${BACKUP_DIR}${NC}"
    
    mkdir -p "$BACKUP_DIR"
    
    # Use rsync for more efficient copying, excluding node_modules and .git
    if command -v rsync &> /dev/null; then
        rsync -a --exclude="node_modules" --exclude=".git" --exclude="*.jar" . "$BACKUP_DIR/"
    else
        # Fallback to cp if rsync is not available
        cp -R . "$BACKUP_DIR"
        # Remove large directories from backup to save space
        rm -rf "$BACKUP_DIR/node_modules" "$BACKUP_DIR/.git" 2>/dev/null || true
    fi
    
    log_message "${GREEN}Backup created successfully at: ${BACKUP_DIR}${NC}"
    add_to_report "Backup created at: ${BACKUP_DIR}"
    return 0
}

# Function to handle special cases
handle_special_cases() {
    log_message "${BLUE}${BOLD}Handling special cases...${NC}"
    
    # Special case 1: Database URLs in configuration files
    log_message "${YELLOW}Handling database URLs in configuration files...${NC}"
    find . -type f \( -name "*.yml" -o -name "*.properties" -o -name "*.xml" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Look for JDBC URLs with chat2db
        if grep -q "jdbc:.*chat2db" "$file"; then
            log_message "Updating JDBC URL in: $file"
            sed -i '' 's/jdbc:\([^:]*:\).*chat2db/jdbc:\1.*magicdb/g' "$file"
            add_to_report "- Updated JDBC URL in: $file"
        fi
    done
    
    # Special case 2: Class names with Chat2DB
    log_message "${YELLOW}Handling class names with Chat2DB...${NC}"
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Look for class definitions with Chat2DB
        if grep -q "class.*Chat2DB" "$file"; then
            log_message "Updating class name in: $file"
            sed -i '' 's/class \([[:alnum:]]*\)Chat2DB/class \1MagicDB/g' "$file"
            add_to_report "- Updated class name in: $file"
        fi
    done
    
    # Special case 3: Constants and enum values
    log_message "${YELLOW}Handling constants and enum values...${NC}"
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Look for constants with CHAT2DB
        if grep -q "CHAT2DB" "$file"; then
            log_message "Updating constants in: $file"
            sed -i '' 's/CHAT2DB/MAGICDB/g' "$file"
            add_to_report "- Updated constants in: $file"
        fi
    done
    
    # Special case 4: SQL queries with chat2db
    log_message "${YELLOW}Handling SQL queries with chat2db...${NC}"
    find . -type f \( -name "*.java" -o -name "*.kt" -o -name "*.xml" -o -name "*.sql" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Look for SQL queries with chat2db
        if grep -q "FROM.*chat2db" "$file" || grep -q "INTO.*chat2db" "$file" || grep -q "UPDATE.*chat2db" "$file"; then
            log_message "Updating SQL query in: $file"
            sed -i '' 's/FROM \([[:alnum:]]*\)chat2db/FROM \1magicdb/g' "$file"
            sed -i '' 's/INTO \([[:alnum:]]*\)chat2db/INTO \1magicdb/g' "$file"
            sed -i '' 's/UPDATE \([[:alnum:]]*\)chat2db/UPDATE \1magicdb/g' "$file"
            add_to_report "- Updated SQL query in: $file"
        fi
    done
    
    return 0
}

# Function to perform the renaming
perform_renaming() {
    log_message "${BLUE}${BOLD}Performing renaming from chat2db to magicdb...${NC}"
    
    # Counter for modified files
    modified_files=0
    renamed_files=0
    renamed_dirs=0
    
    # Step 1: Replace content in files
    log_message "${BLUE}Step 1: Replacing content in files...${NC}"
    
    # Extensions to process
    extensions=("java" "kt" "xml" "json" "yml" "yaml" "properties" "md" "sh" "ts" "js" "html" "css" "sql" "txt" "gradle" "kts")
    
    for ext in "${extensions[@]}"; do
        log_message "${YELLOW}Processing .$ext files...${NC}"
        
        # Find all files with the extension
        find . -type f -name "*.$ext" \
            -not -path "*/\.*" \
            -not -path "*/node_modules/*" \
            -not -path "*/target/*" \
            -not -path "*/build/*" \
            -not -path "./advanced_rename.sh" | while read file; do
            
            # Check if file contains any form of "chat2db"
            if grep -q -i "chat2db" "$file" 2>/dev/null; then
                log_message "Updating content in: $file"
                
                # Replace with case preservation for macOS
                sed -i '' 's/chat2db/magicdb/g' "$file"
                sed -i '' 's/Chat2DB/MagicDB/g' "$file"
                sed -i '' 's/Chat2db/Magicdb/g' "$file"
                sed -i '' 's/CHAT2DB/MAGICDB/g' "$file"
                sed -i '' 's/ai\.chat2db/ai\.magicdb/g' "$file"
                
                # Handle special cases for URLs and paths
                sed -i '' 's/\.chat2db\./\.magicdb\./g' "$file"
                sed -i '' 's/\/chat2db\//\/magicdb\//g' "$file"
                
                # Handle database paths
                sed -i '' 's/~\/\.chat2db\//~\/\.magicdb\//g' "$file"
                
                modified_files=$((modified_files + 1))
                add_to_report "- Modified: $file"
            fi
        done
    done
    
    # Step 2: Update Java/Kotlin package declarations
    log_message "${BLUE}Step 2: Updating Java/Kotlin package declarations...${NC}"
    
    # Find all Java and Kotlin files
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Check if file contains package declaration with chat2db
        if grep -q "package.*chat2db" "$file"; then
            log_message "Updating package in: $file"
            sed -i '' 's/package \([[:alnum:]\.]*\)chat2db/package \1magicdb/g' "$file"
        fi
    done
    
    # Step 3: Update import statements
    log_message "${BLUE}Step 3: Updating import statements...${NC}"
    
    # Find all Java and Kotlin files
    find . -type f \( -name "*.java" -o -name "*.kt" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Check if file contains import statements with chat2db
        if grep -q "import.*chat2db" "$file"; then
            log_message "Updating imports in: $file"
            sed -i '' 's/import \([[:alnum:]\.]*\)chat2db/import \1magicdb/g' "$file"
        fi
    done
    
    # Step 4: Handle special cases
    handle_special_cases
    
    # Step 5: Rename files
    log_message "${BLUE}Step 5: Renaming files...${NC}"
    
    # Find all files with "chat2db" in their name
    find . -type f -name "*chat2db*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "./advanced_rename.sh" | while read file; do
        
        dir_name=$(dirname "$file")
        base_name=$(basename "$file")
        new_base_name=$(echo "$base_name" | sed 's/chat2db/magicdb/g; s/Chat2DB/MagicDB/g; s/Chat2db/Magicdb/g')
        new_name="${dir_name}/${new_base_name}"
        
        if [ "$file" != "$new_name" ]; then
            log_message "Renaming file: $file -> $new_name"
            mv "$file" "$new_name"
            renamed_files=$((renamed_files + 1))
            add_to_report "- Renamed file: $file -> $new_name"
        fi
    done
    
    # Step 6: Rename directories (bottom-up to avoid path issues)
    log_message "${BLUE}Step 6: Renaming directories...${NC}"
    
    # Find all directories with "chat2db" in their name, sort in reverse order (deepest first)
    find . -type d -name "*chat2db*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | sort -r | while read dir; do
        
        parent_dir=$(dirname "$dir")
        base_dir=$(basename "$dir")
        new_base_dir=$(echo "$base_dir" | sed 's/chat2db/magicdb/g; s/Chat2DB/MagicDB/g; s/Chat2db/Magicdb/g')
        new_dir="${parent_dir}/${new_base_dir}"
        
        if [ "$dir" != "$new_dir" ] && [ -d "$dir" ]; then
            log_message "Renaming directory: $dir -> $new_dir"
            
            # Create the new directory
            mkdir -p "$new_dir"
            
            # Move all contents (handle hidden files separately for macOS)
            mv "$dir"/* "$new_dir" 2>/dev/null || true
            
            # Move hidden files (if any)
            for hidden_file in "$dir"/.[!.]*; do
                if [ -e "$hidden_file" ]; then
                    mv "$hidden_file" "$new_dir/" 2>/dev/null || true
                fi
            done
            
            # Remove the old directory
            rmdir "$dir" 2>/dev/null || true
            
            renamed_dirs=$((renamed_dirs + 1))
            add_to_report "- Renamed directory: $dir -> $new_dir"
        fi
    done
    
    # Update report summary
    add_to_report "## Summary\n" -i
    add_to_report "- Files modified: $modified_files" -i
    add_to_report "- Files renamed: $renamed_files" -i
    add_to_report "- Directories renamed: $renamed_dirs" -i
    
    return 0
}

# Function to verify the renaming
verify_renaming() {
    log_message "${BLUE}${BOLD}Verifying the renaming process...${NC}"
    
    # Check for remaining instances of chat2db
    log_message "${BLUE}Checking for remaining instances of chat2db...${NC}"
    
    # Find all text files
    remaining_files=$(find . -type f \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "./advanced_rename.sh" \
        -exec grep -l "chat2db" {} \; 2>/dev/null)
    
    if [ -n "$remaining_files" ]; then
        log_message "${RED}Found remaining instances of chat2db in the following files:${NC}"
        log_message "$remaining_files"
        
        log_message "\n${YELLOW}Details of remaining instances:${NC}"
        add_to_report "## Potential Issues\n" -i
        add_to_report "The following files still contain references to chat2db:" -i
        
        for file in $remaining_files; do
            log_message "${RED}File: $file${NC}"
            instances=$(grep -n "chat2db" "$file" | head -10)  # Show first 10 instances
            log_message "$instances"
            log_message ""
            
            add_to_report "- $file" -i
            add_to_report "  ```" -i
            add_to_report "  $instances" -i
            add_to_report "  ```" -i
        done
        
        return 1
    else
        log_message "${GREEN}No remaining instances of chat2db found in text files!${NC}"
        add_to_report "No remaining instances of chat2db found in text files." -i
    fi
    
    # Check for remaining directories
    remaining_dirs=$(find . -type d -name "*chat2db*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" 2>/dev/null)
    
    if [ -n "$remaining_dirs" ]; then
        log_message "${RED}Found remaining directories with chat2db in their name:${NC}"
        log_message "$remaining_dirs"
        
        add_to_report "The following directories still contain chat2db in their name:" -i
        for dir in $remaining_dirs; do
            add_to_report "- $dir" -i
        done
        
        return 1
    else
        log_message "${GREEN}No remaining directories with chat2db in their name!${NC}"
        add_to_report "No remaining directories with chat2db in their name." -i
    fi
    
    # Check for remaining files
    remaining_named_files=$(find . -type f -name "*chat2db*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "./advanced_rename.sh" 2>/dev/null)
    
    if [ -n "$remaining_named_files" ]; then
        log_message "${RED}Found remaining files with chat2db in their name:${NC}"
        log_message "$remaining_named_files"
        
        add_to_report "The following files still contain chat2db in their name:" -i
        for file in $remaining_named_files; do
            add_to_report "- $file" -i
        done
        
        return 1
    else
        log_message "${GREEN}No remaining files with chat2db in their name!${NC}"
        add_to_report "No remaining files with chat2db in their name." -i
    fi
    
    return 0
}

# Function to provide post-renaming guidance
provide_guidance() {
    log_message "\n${BLUE}${BOLD}Post-Renaming Guidance:${NC}"
    log_message "${YELLOW}1. Clean and rebuild the project:${NC}"
    log_message "   - For Maven: mvn clean install -DskipTests"
    log_message "   - For client: cd magicdb-client && yarn install && yarn build"
    
    log_message "\n${YELLOW}2. Update IDE configurations:${NC}"
    log_message "   - Refresh/reload your project in your IDE"
    log_message "   - Update any run configurations"
    
    log_message "\n${YELLOW}3. Test the application:${NC}"
    log_message "   - Run unit tests"
    log_message "   - Start the application and verify basic functionality"
    
    log_message "\n${YELLOW}4. Check for any remaining issues:${NC}"
    log_message "   - Look for any runtime errors related to the renaming"
    log_message "   - Check logs for any 'chat2db' references that might have been missed"
    
    log_message "\n${GREEN}${BOLD}Renaming log has been saved to: ${LOG_FILE}${NC}"
    log_message "${GREEN}${BOLD}Renaming report has been saved to: ${REPORT_FILE}${NC}"
    log_message "${GREEN}${BOLD}A backup of your original project was created at: ${BACKUP_DIR}${NC}"
    
    # Add guidance to report
    add_to_report "\n## Next Steps\n"
    add_to_report "1. Clean and rebuild the project:"
    add_to_report "   - For Maven: `mvn clean install -DskipTests`"
    add_to_report "   - For client: `cd magicdb-client && yarn install && yarn build`"
    add_to_report ""
    add_to_report "2. Update IDE configurations:"
    add_to_report "   - Refresh/reload your project in your IDE"
    add_to_report "   - Update any run configurations"
    add_to_report ""
    add_to_report "3. Test the application:"
    add_to_report "   - Run unit tests"
    add_to_report "   - Start the application and verify basic functionality"
    add_to_report ""
    add_to_report "4. Check for any remaining issues:"
    add_to_report "   - Look for any runtime errors related to the renaming"
    add_to_report "   - Check logs for any 'chat2db' references that might have been missed"
}

# Main execution
log_message "${BLUE}${BOLD}=== Starting Advanced Chat2DB to MagicDB Renaming Process ===${NC}"
log_message "Timestamp: $(date)"

initialize_report

if ! create_backup; then
    log_message "${RED}${BOLD}Backup creation failed. Aborting.${NC}"
    exit 1
fi

if ! perform_renaming; then
    log_message "${RED}${BOLD}Renaming process failed. Please check the log for details.${NC}"
    log_message "${YELLOW}You can restore from the backup at: ${BACKUP_DIR}${NC}"
    exit 1
fi

if ! verify_renaming; then
    log_message "${RED}${BOLD}Verification failed. Some instances of chat2db remain in the codebase.${NC}"
    log_message "${YELLOW}Please review the log and fix the remaining instances manually.${NC}"
    provide_guidance
    exit 1
else
    log_message "${GREEN}${BOLD}Verification successful! All instances of chat2db have been renamed to magicdb.${NC}"
    provide_guidance
    exit 0
fi
