#!/bin/bash

# Master script to orchestrate the entire renaming process from magicdb to magicdb
# This script will:
# 1. Perform pre-checks to ensure the environment is ready
# 2. Create a backup of the project
# 3. Execute the renaming process
# 4. Verify the results
# 5. Provide guidance for post-renaming steps

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
LOG_FILE="rename_log_${TIMESTAMP}.txt"

# Function to log messages
log_message() {
    echo -e "$1" | tee -a "$LOG_FILE"
}

# Function to check prerequisites
check_prerequisites() {
    log_message "${BLUE}${BOLD}Checking prerequisites...${NC}"
    
    # Check if running on macOS
    if [[ "$(uname)" != "Darwin" ]]; then
        log_message "${RED}This script is designed for macOS. Please use a macOS system or adapt the script for your OS.${NC}"
        return 1
    fi
    
    # Check if required tools are installed
    for tool in find sed grep; do
        if ! command -v $tool &> /dev/null; then
            log_message "${RED}Required tool '$tool' is not installed. Please install it and try again.${NC}"
            return 1
        fi
    done
    
    # Check if the project structure looks like magicdb
    if [ ! -d "./magicdb-server" ] || [ ! -d "./magicdb-client" ]; then
        log_message "${RED}The current directory doesn't appear to be the magicdb project root.${NC}"
        log_message "${RED}Please run this script from the root of the magicdb project.${NC}"
        return 1
    fi
    
    # Check for uncommitted changes if git is used
    if [ -d ".git" ]; then
        if ! git diff --quiet; then
            log_message "${YELLOW}Warning: You have uncommitted changes in your git repository.${NC}"
            log_message "${YELLOW}It's recommended to commit or stash your changes before proceeding.${NC}"
            read -p "Do you want to continue anyway? (y/n) " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                log_message "${RED}Aborting as requested.${NC}"
                return 1
            fi
        fi
    fi
    
    return 0
}

# Function to create a backup
create_backup() {
    log_message "${BLUE}${BOLD}Creating backup of the project...${NC}"
    
    BACKUP_DIR="../magicdb_backup_${TIMESTAMP}"
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
    return 0
}

# Function to perform the renaming
perform_renaming() {
    log_message "${BLUE}${BOLD}Performing renaming from magicdb to magicdb...${NC}"
    
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
            -not -path "./master_rename.sh" | while read file; do
            
            # Check if file contains any form of "magicdb"
            if grep -q -i "magicdb" "$file" 2>/dev/null; then
                log_message "Updating content in: $file"
                
                # Replace with case preservation for macOS
                sed -i '' 's/magicdb/magicdb/g' "$file"
                sed -i '' 's/MagicDB/MagicDB/g' "$file"
                sed -i '' 's/Magicdb/Magicdb/g' "$file"
                sed -i '' 's/MAGICDB/MAGICDB/g' "$file"
                sed -i '' 's/ai\.magicdb/ai\.magicdb/g' "$file"
                
                # Handle special cases for URLs and paths
                sed -i '' 's/\.magicdb\./\.magicdb\./g' "$file"
                sed -i '' 's/\/magicdb\//\/magicdb\//g' "$file"
                
                # Handle database paths
                sed -i '' 's/~\/\.magicdb\//~\/\.magicdb\//g' "$file"
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
        
        # Check if file contains package declaration with magicdb
        if grep -q "package.*magicdb" "$file"; then
            log_message "Updating package in: $file"
            sed -i '' 's/package \([[:alnum:]\.]*\)magicdb/package \1magicdb/g' "$file"
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
        
        # Check if file contains import statements with magicdb
        if grep -q "import.*magicdb" "$file"; then
            log_message "Updating imports in: $file"
            sed -i '' 's/import \([[:alnum:]\.]*\)magicdb/import \1magicdb/g' "$file"
        fi
    done
    
    # Step 4: Update Maven POM files
    log_message "${BLUE}Step 4: Updating Maven POM files...${NC}"
    
    # Find all pom.xml files
    find . -name "pom.xml" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Check if file contains magicdb
        if grep -q "magicdb" "$file"; then
            log_message "Updating POM file: $file"
            
            # Replace groupId, artifactId, and dependencies
            sed -i '' 's/<groupId>ai\.magicdb<\/groupId>/<groupId>ai\.magicdb<\/groupId>/g' "$file"
            sed -i '' 's/<artifactId>magicdb/<artifactId>magicdb/g' "$file"
            sed -i '' 's/<name>magicdb/<name>magicdb/g' "$file"
            sed -i '' 's/<n>magicdb/<n>magicdb/g' "$file"
        fi
    done
    
    # Step 5: Update configuration files
    log_message "${BLUE}Step 5: Updating configuration files...${NC}"
    
    # Find all configuration files
    find . \( -name "*.yml" -o -name "*.yaml" -o -name "*.properties" -o -name "*.json" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Check if file contains magicdb
        if grep -q "magicdb" "$file"; then
            log_message "Updating configuration file: $file"
            
            # Replace configuration keys and values
            sed -i '' 's/magicdb:/magicdb:/g' "$file"
            sed -i '' 's/"magicdb"/"magicdb"/g' "$file"
            sed -i '' 's/\.magicdb\./\.magicdb\./g' "$file"
            
            # Handle database paths
            sed -i '' 's/~\/\.magicdb/~\/\.magicdb/g' "$file"
        fi
    done
    
    # Step 6: Update build scripts
    log_message "${BLUE}Step 6: Updating build scripts...${NC}"
    
    # Find all shell scripts and build files
    find . \( -name "*.sh" -o -name "*.gradle" -o -name "*.kts" \) \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "./master_rename.sh" | while read file; do
        
        # Check if file contains magicdb
        if grep -q "magicdb" "$file"; then
            log_message "Updating build script: $file"
            
            # Replace in build scripts
            sed -i '' 's/magicdb/magicdb/g' "$file"
            sed -i '' 's/MagicDB/MagicDB/g' "$file"
            sed -i '' 's/Magicdb/Magicdb/g' "$file"
        fi
    done
    
    # Step 7: Update package.json files
    log_message "${BLUE}Step 7: Updating package.json files...${NC}"
    
    # Find all package.json files
    find . -name "package.json" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        # Check if file contains magicdb
        if grep -q "magicdb" "$file"; then
            log_message "Updating package.json: $file"
            
            # Replace in package.json
            sed -i '' 's/"name": "magicdb/"name": "magicdb/g' "$file"
            sed -i '' 's/magicdb/magicdb/g' "$file"
        fi
    done
    
    # Step 8: Rename files
    log_message "${BLUE}Step 8: Renaming files...${NC}"
    
    # Find all files with "magicdb" in their name
    find . -type f -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | while read file; do
        
        dir_name=$(dirname "$file")
        base_name=$(basename "$file")
        new_base_name=$(echo "$base_name" | sed 's/magicdb/magicdb/g; s/MagicDB/MagicDB/g; s/Magicdb/Magicdb/g')
        new_name="${dir_name}/${new_base_name}"
        
        if [ "$file" != "$new_name" ]; then
            log_message "Renaming file: $file -> $new_name"
            mv "$file" "$new_name"
        fi
    done
    
    # Step 9: Rename directories (bottom-up to avoid path issues)
    log_message "${BLUE}Step 9: Renaming directories...${NC}"
    
    # Find all directories with "magicdb" in their name, sort in reverse order (deepest first)
    find . -type d -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" | sort -r | while read dir; do
        
        parent_dir=$(dirname "$dir")
        base_dir=$(basename "$dir")
        new_base_dir=$(echo "$base_dir" | sed 's/magicdb/magicdb/g; s/MagicDB/MagicDB/g; s/Magicdb/Magicdb/g')
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
        fi
    done
    
    return 0
}

# Function to verify the renaming
verify_renaming() {
    log_message "${BLUE}${BOLD}Verifying the renaming process...${NC}"
    
    # Check for remaining instances of magicdb
    log_message "${BLUE}Checking for remaining instances of magicdb...${NC}"
    
    # Find all text files
    remaining_files=$(find . -type f \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "./master_rename.sh" \
        -exec grep -l "magicdb" {} \; 2>/dev/null)
    
    if [ -n "$remaining_files" ]; then
        log_message "${RED}Found remaining instances of magicdb in the following files:${NC}"
        log_message "$remaining_files"
        
        log_message "\n${YELLOW}Details of remaining instances:${NC}"
        for file in $remaining_files; do
            log_message "${RED}File: $file${NC}"
            grep -n "magicdb" "$file" | head -10  # Show first 10 instances
            log_message ""
        done
        
        return 1
    else
        log_message "${GREEN}No remaining instances of magicdb found in text files!${NC}"
    fi
    
    # Check for remaining directories
    remaining_dirs=$(find . -type d -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" 2>/dev/null)
    
    if [ -n "$remaining_dirs" ]; then
        log_message "${RED}Found remaining directories with magicdb in their name:${NC}"
        log_message "$remaining_dirs"
        return 1
    else
        log_message "${GREEN}No remaining directories with magicdb in their name!${NC}"
    fi
    
    # Check for remaining files
    remaining_named_files=$(find . -type f -name "*magicdb*" \
        -not -path "*/\.*" \
        -not -path "*/node_modules/*" \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "./master_rename.sh" 2>/dev/null)
    
    if [ -n "$remaining_named_files" ]; then
        log_message "${RED}Found remaining files with magicdb in their name:${NC}"
        log_message "$remaining_named_files"
        return 1
    else
        log_message "${GREEN}No remaining files with magicdb in their name!${NC}"
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
    log_message "   - Check logs for any 'magicdb' references that might have been missed"
    
    log_message "\n${YELLOW}5. Update external references:${NC}"
    log_message "   - Update any documentation or external references to the project"
    log_message "   - Update CI/CD configurations if applicable"
    
    log_message "\n${GREEN}${BOLD}Renaming log has been saved to: ${LOG_FILE}${NC}"
    log_message "${GREEN}${BOLD}A backup of your original project was created at: ${BACKUP_DIR}${NC}"
}

# Main execution
log_message "${BLUE}${BOLD}=== Starting magicdb to magicdb Renaming Process ===${NC}"
log_message "Timestamp: $(date)"

if ! check_prerequisites; then
    log_message "${RED}${BOLD}Prerequisites check failed. Aborting.${NC}"
    exit 1
fi

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
    log_message "${RED}${BOLD}Verification failed. Some instances of magicdb remain in the codebase.${NC}"
    log_message "${YELLOW}Please review the log and fix the remaining instances manually.${NC}"
    provide_guidance
    exit 1
else
    log_message "${GREEN}${BOLD}Verification successful! All instances of magicdb have been renamed to magicdb.${NC}"
    provide_guidance
    exit 0
fi
