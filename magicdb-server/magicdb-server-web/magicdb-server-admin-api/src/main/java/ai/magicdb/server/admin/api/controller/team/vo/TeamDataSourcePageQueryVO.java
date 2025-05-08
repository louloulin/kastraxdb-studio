
package ai.magicdb.server.admin.api.controller.team.vo;

import ai.magicdb.server.admin.api.controller.datasource.vo.SimpleDataSourceVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamDataSourcePageQueryVO {

    /**
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * team id
     */
    private Long teamId;

    /**
     * Data Source
     */
    private SimpleDataSourceVO dataSource;
}
