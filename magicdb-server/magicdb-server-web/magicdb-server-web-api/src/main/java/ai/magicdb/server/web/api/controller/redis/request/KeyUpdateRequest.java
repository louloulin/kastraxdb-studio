package ai.magicdb.server.web.api.controller.redis.request;

import jakarta.validation.constraints.NotNull;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionQueryRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class KeyUpdateRequest extends DataSourceBaseRequest {

    /**
     * key name
     */
    @NotNull
    private String originalKey;

    /**
     * Key name after update
     */
    private String updateKey;

    /**
     * Original ttl value
     */
    private Long originalTtl;

    /**
     * ttl value after update
     */
    private Object updateTtl;
}
