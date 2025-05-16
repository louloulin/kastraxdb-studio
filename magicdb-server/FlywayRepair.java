import org.flywaydb.core.Flyway;

public class FlywayRepair {
    public static void main(String[] args) {
        String dbPath = System.getProperty("user.home") + "/.magicdb/db/magicdb_dev";
        String url = "jdbc:h2:" + dbPath + ";FILE_LOCK=NO;MODE=MYSQL";
        
        System.out.println("Running Flyway repair on database: " + url);
        
        Flyway flyway = Flyway.configure()
            .dataSource(url, "", "")
            .load();
            
        flyway.repair();
        
        System.out.println("Flyway repair completed successfully");
    }
}
