package ai.magicdb.server.web.api.controller.rdb.request;

import java.io.Serial;

import jakarta.validation.constraints.NotNull;

import ai.magicdb.server.tools.base.wrapper.request.PageQueryRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author moji
 * @version ConnectionQueryRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableBriefQueryRequest extends PageQueryRequest implements DataSourceBaseRequestInfo {

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public Long getDataSourceId() {
        return this.dataSourceId;
    }

    @Serial
    private static final long serialVersionUID = -364547173428396332L;
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
     * The space where the table is located is required by pg and oracle, but not by mysql.
     */
    private String schemaName;

    /**
     * Fuzzy search terms
     */
    private String searchKey;

    /**
     * if true, refresh the cache
     */
    private boolean refresh;

}
