package ai.magicdb.server.web.start.config.config;

import ai.magicdb.server.tools.common.config.MagicdbProperties;
import com.dtflys.forest.Forest;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * forest config
 *
 * @author Jiaju Zhuang
 */
@Configuration
public class MagicdbForestConfiguration implements InitializingBean {

    @Resource
    private MagicdbProperties magicdbProperties;
    @Override
    public void afterPropertiesSet() throws Exception {
        Forest.config()
            .setVariableValue("gatewayBaseUrl", magicdbProperties.getGateway().getBaseUrl());
    }
}
