# MagicDB to MagicDB Renaming Process

This document provides instructions for renaming the MagicDB project to MagicDB, including all necessary steps and scripts.

## Overview

The renaming process involves:

1. Replacing all occurrences of "magicdb" with "magicdb" in file contents
2. Updating Java/Kotlin package declarations and import statements
3. Renaming files and directories
4. Migrating database configurations and data
5. Verifying the renaming was successful

## Scripts Provided

We've created several scripts to help with the renaming process:

1. **master_rename.sh** - The main script that orchestrates the entire renaming process
2. **database_migration.sh** - Specialized script for handling database migration
3. **rename_magicdb_to_magicdb_complete.sh** - Standalone script for the core renaming process
4. **fix_java_packages.sh** - Script to fix Java/Kotlin package declarations
5. **verify_rename.sh** - Script to verify that all instances of "magicdb" have been renamed

## Prerequisites

- macOS operating system (the scripts use macOS-specific commands)
- Bash shell
- Standard Unix tools: find, sed, grep
- Git (optional, for version control)

## Step-by-Step Instructions

### 1. Preparation

Before starting the renaming process, it's recommended to:

- Create a backup of your project
- Commit any pending changes to your version control system
- Close any IDEs or editors that might be accessing the project files

### 2. Run the Master Script

The master script handles the entire renaming process:

```bash
./master_rename.sh
```

This script will:
- Perform pre-checks to ensure the environment is ready
- Create a backup of the project
- Execute the renaming process
- Verify the results
- Provide guidance for post-renaming steps

### 3. Database Migration

After running the master script, run the database migration script:

```bash
./database_migration.sh
```

This script will:
- Identify database paths and configuration
- Create a backup of the database
- Update database paths and configuration
- Migrate data from ~/.magicdb to ~/.magicdb

### 4. Post-Renaming Steps

After the renaming process is complete:

1. **Clean and rebuild the project**:
   - For Maven: `mvn clean install -DskipTests`
   - For client: `cd magicdb-client && yarn install && yarn build`

2. **Update IDE configurations**:
   - Refresh/reload your project in your IDE
   - Update any run configurations

3. **Test the application**:
   - Run unit tests
   - Start the application and verify basic functionality

4. **Check for any remaining issues**:
   - Look for any runtime errors related to the renaming
   - Check logs for any 'magicdb' references that might have been missed

## Troubleshooting

### If the master script fails:

1. Check the log file (rename_log_*.txt) for details
2. Fix any issues manually
3. Run the verification script to check if all instances have been renamed:
   ```bash
   ./verify_rename.sh
   ```

### If you need to fix Java/Kotlin packages:

Run the package fix script:
```bash
./fix_java_packages.sh
```

### If you encounter database issues:

1. Check the database migration log (db_migration_log_*.txt)
2. Restore from the backup created by the database migration script
3. Follow the guidance provided in the log file

## Manual Verification

Even after running the scripts, it's recommended to manually verify:

1. **Project structure**: Ensure all directories and files have been renamed correctly
2. **Code compilation**: Make sure the project compiles without errors
3. **Application startup**: Verify that the application starts correctly
4. **Basic functionality**: Test basic features to ensure they work as expected
5. **Database access**: Confirm that the application can access the database correctly

## Reverting to Original

If you need to revert to the original MagicDB project:

1. Restore from the backup created by the master script
2. If you've migrated the database, restore it from the backup created by the database migration script

## Support

If you encounter any issues during the renaming process, please:

1. Check the log files for details
2. Review this documentation for guidance
3. Reach out to the project maintainers for assistance

---

**Note**: These scripts are provided as-is and should be used with caution. Always ensure you have a backup before proceeding with any renaming process.
