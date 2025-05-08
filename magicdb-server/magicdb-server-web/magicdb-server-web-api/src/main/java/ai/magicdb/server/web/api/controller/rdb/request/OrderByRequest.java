package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import ai.magicdb.spi.model.OrderBy;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderByRequest extends DataSourceBaseRequest implements DataSourceBaseRequestInfo {

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }

    @Override
    public Long getDataSourceId() {
        return super.getDataSourceId();
    }

    /**
     * origin sql
     */
    private String originSql;

    /**
     * sort field
     */
    private List<OrderBy> orderByList;

}
