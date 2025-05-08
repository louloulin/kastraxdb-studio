package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.domain.api.param.SelectResultOperation;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;
import ai.magicdb.spi.model.Header;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class SelectResultUpdateRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }

    @Override
    public Long getDataSourceId() {
        return super.getDataSourceId();
    }

    /**
     * List of display headers
     */
    private List<Header> headerList;

    /**
     * List of modified data
     */
    @NotEmpty
    private List<SelectResultOperation> operations;

    /**
     * Table Name
     */
    private String tableName;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;
    @Override
    public Long getConsoleId() {
        return consoleId;
    }

}
