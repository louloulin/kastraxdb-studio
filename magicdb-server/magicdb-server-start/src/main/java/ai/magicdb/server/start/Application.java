package ai.magicdb.server.start;

import ai.magicdb.server.domain.repository.Dbutils;
import ai.magicdb.server.tools.common.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Indexed;


/**
 * Startup class
 *
 * @author Jiaju Zhuang
 */
@SpringBootApplication
@ComponentScan(value = {"ai.magicdb.server"})
@Indexed
@EnableCaching
@EnableScheduling
@EnableAsync
@Slf4j
public class Application {

    public static void main(String[] args) {
        ConfigUtils.initProcess();
        new Thread(() -> {
            Dbutils.init();
        }).start();
        SpringApplication.run(Application.class, args);
    }
}
