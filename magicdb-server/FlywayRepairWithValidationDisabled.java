import org.flywaydb.core.Flyway;

public class FlywayRepairWithValidationDisabled {
    public static void main(String[] args) {
        String dbPath = System.getProperty("user.home") + "/.magicdb/db/magicdb_dev";
        String url = "jdbc:h2:" + dbPath + ";FILE_LOCK=NO;MODE=MYSQL";
        
        System.out.println("Running Flyway repair on database: " + url);
        
        Flyway flyway = Flyway.configure()
            .dataSource(url, "", "")
            .validateOnMigrate(false)  // Disable validation
            .load();
            
        // Run repair to fix the schema history table
        flyway.repair();
        
        System.out.println("Flyway repair completed successfully");
        
        // Now run clean to remove all objects in the database
        System.out.println("Running Flyway clean to reset the database...");
        flyway.clean();
        System.out.println("Flyway clean completed successfully");
        
        // Now run migrate to recreate the database with the current migration files
        System.out.println("Running Flyway migrate to recreate the database...");
        flyway.migrate();
        System.out.println("Flyway migrate completed successfully");
    }
}
