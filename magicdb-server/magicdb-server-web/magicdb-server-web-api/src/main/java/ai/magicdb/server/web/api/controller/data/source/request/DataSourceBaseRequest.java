package ai.magicdb.server.web.api.controller.data.source.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author moji
 * @version MysqlBaseRequest.java, v 0.1 September 18, 2022 11:51 moji Exp $
 * @date 2022/09/18
 */
@Data
@Getter
@Setter
public class DataSourceBaseRequest implements DataSourceBaseRequestInfo {

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public Long getDataSourceId() {
        return this.dataSourceId;
    }

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;


    /**
     * if true, refresh the cache
     */
    private boolean refresh;
}
