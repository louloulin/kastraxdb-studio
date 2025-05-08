package ai.magicdb.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version TableManageRequest.java, v 0.1 September 16, 2022 17:55 moji Exp $
 * @date 2022/09/16
 */
@Data
public class TableDeleteRequest extends DataSourceBaseRequest {

    /**
     * Table Name
     */
    @NotNull
    private String tableName;
}
