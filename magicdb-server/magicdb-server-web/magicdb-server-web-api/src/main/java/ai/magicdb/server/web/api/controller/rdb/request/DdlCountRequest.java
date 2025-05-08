package ai.magicdb.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * total number
 *
 * @author Jiaju Zhuang
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DdlCountRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    @Override
    public Long getConsoleId() {
        return this.consoleId;
    }

    /**
     * sql statement
     */
    @NotNull
    private String sql;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;
}
