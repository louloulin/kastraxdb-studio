
package ai.magicdb.server.start.config.config;

import java.util.ArrayList;
import java.util.List;

import ai.magicdb.spi.sql.MagicDBContext;
import ai.magicdb.spi.util.JdbcJarUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author jipengfei
 * @version : JarDownloadTask.java
 */
@Component
@Slf4j
public class JarDownloadTask implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        List<String> urls = new ArrayList<>();
        MagicDBContext.PLUGIN_MAP.forEach((k, v) -> {
            v.getDBConfig().getDriverConfigList().forEach(driverConfig -> {
                if (driverConfig != null && !CollectionUtils.isEmpty(driverConfig.getDownloadJdbcDriverUrls()) && (
                    "MYSQL".equals(driverConfig.getDbType()))) {
                    urls.addAll(driverConfig.getDownloadJdbcDriverUrls());
                }
            });
        });
        JdbcJarUtils.asyncDownload(urls);
    }
}