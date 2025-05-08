package ai.magicdb.server.web.api.controller.ai.magicdb.client;

import ai.magicdb.server.domain.api.model.Config;
import ai.magicdb.server.domain.api.service.ConfigService;
import ai.magicdb.server.web.api.util.ApplicationContextUtil;
import com.unfbx.chatgpt.constant.OpenAIConst;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author jipengfei
 * @version : OpenAIClient.java
 */
@Slf4j
public class MagicdbAIClient {

    public static final String MAGICDB_OPENAI_KEY = "magicdb.apiKey";

    /**
     * OPENAI interface domain name
     */
    public static final String MAGICDB_OPENAI_HOST = "magicdb.apiHost";

    /**
     * OPENAI model
     */
    public static final String MAGICDB_OPENAI_MODEL = "magicdb.model";

    /**
     * FASTCHAT OPENAI embedding model
     */
    public static final String MAGICDB_EMBEDDING_MODEL= "fastchat.embedding.model";


    private static volatile MagicDBAIStreamClient MAGICDB_AI_STREAM_CLIENT;

    public static MagicDBAIStreamClient getInstance() {
        if (MAGICDB_AI_STREAM_CLIENT != null) {
            return MAGICDB_AI_STREAM_CLIENT;
        } else {
            return singleton();
        }
    }

    private static MagicDBAIStreamClient singleton() {
        if (MAGICDB_AI_STREAM_CLIENT == null) {
            synchronized (MagicdbAIClient.class) {
                if (MAGICDB_AI_STREAM_CLIENT == null) {
                    refresh();
                }
            }
        }
        return MAGICDB_AI_STREAM_CLIENT;
    }

    public static void refresh() {
        ConfigService configService = ApplicationContextUtil.getBean(ConfigService.class);

        MAGICDB_AI_STREAM_CLIENT = MagicDBAIStreamClient.builder().apiHost(getApiHost(configService))
                .apiKey(getApiKey(configService)).model(getModel(configService)).build();
    }

    private static String getApiHost(ConfigService configService) {
        Config apiHostConfig = configService.find(MAGICDB_OPENAI_HOST).getData();

        if (Objects.nonNull(apiHostConfig)) {
            return apiHostConfig.getContent();
        }

        String apiHost = ApplicationContextUtil.getProperty(MAGICDB_OPENAI_HOST);

        if (apiHost.isBlank()) {
            return OpenAIConst.OPENAI_HOST;
        }

        return apiHost;
    }

    private static String getApiKey(ConfigService configService) {
        String apiKey;

        Config config = configService.find(MAGICDB_OPENAI_KEY).getData();

        if (Objects.nonNull(config)) {
            apiKey = config.getContent();
        } else {
            apiKey = ApplicationContextUtil.getProperty(MAGICDB_OPENAI_KEY);
        }

        log.info("refresh magicdb apikey:{}", maskApiKey(apiKey));

        return apiKey;
    }

    private static String getModel(ConfigService configService) {
        Config modelConfig = configService.find(MAGICDB_OPENAI_MODEL).getData();

        if (Objects.nonNull(modelConfig)) {
            return modelConfig.getContent();
        }

        return null;
    }

    private static String maskApiKey(String input) {
        if (Objects.isNull(input)) {
            return null;
        }

        StringBuilder maskedString = new StringBuilder(input);
        for (int i = input.length() / 4; i < input.length() / 2; i++) {
            maskedString.setCharAt(i, '*');
        }
        return maskedString.toString();
    }
}
