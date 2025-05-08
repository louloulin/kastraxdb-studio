package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TypeQueryRequest implements DataSourceBaseRequestInfo {

    @NotNull
    private Long dataSourceId;
    /**
     * DB name
     */
    private String databaseName;
}
