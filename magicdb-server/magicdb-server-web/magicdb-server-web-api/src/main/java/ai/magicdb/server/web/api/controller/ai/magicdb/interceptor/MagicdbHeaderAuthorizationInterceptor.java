package ai.magicdb.server.web.api.controller.ai.magicdb.interceptor;

import ai.magicdb.server.domain.api.enums.AiSqlSourceEnum;
import ai.magicdb.server.web.api.util.StringUtils;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Description: Request to add header apikey
 *
 * @author grt
 * @since 2023-03-23
 */
@Getter
public class MagicdbHeaderAuthorizationInterceptor implements Interceptor {

    private String apiKey;

    private String model;

    public MagicdbHeaderAuthorizationInterceptor(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        if (StringUtils.isEmpty(model)) {
            this.model = AiSqlSourceEnum.OPENAI.getCode();
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + apiKey)
                .header("X-MAGICDB-AI-TYPE", model)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
