
package ai.magicdb.server.web.api.controller.rdb.request;

import java.io.Serial;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import ai.magicdb.server.tools.base.wrapper.request.PageQueryRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jipengfei
 * @version : TableColumnQueryRequest.java
 */
@Data
@Getter
@Setter
public class TableQueryRequest extends PageQueryRequest implements DataSourceBaseRequestInfo {

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public Long getDataSourceId() {
        return this.dataSourceId;
    }

    @Serial
    private static final long serialVersionUID = 5794716286491282784L;

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB name
     */
    @NotNull
    private String databaseName;

    /**
     * Table Name
     */
    private String tableName;
}