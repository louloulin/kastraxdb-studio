package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author moji
 * @version TableManageRequest.java, v 0.1 September 16, 2022 17:55 moji Exp $
 * @date 2022/09/16
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DmlTableRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    @Override
    public Long getConsoleId() {
        return this.consoleId;
    }

    /**
     * table noun
     */
    @NotNull
    private String tableName;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;

    /**
     *Page coding
     * Only available for select statements
     */
    private Integer pageNo;

    /**
     * Paging Size
     * Only available for select statements
     */
    private Integer pageSize;

    /**
     * Return all data
     * Only available for select statements
     */
    private Boolean pageSizeAll;
}
