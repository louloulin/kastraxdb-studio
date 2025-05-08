
package ai.magicdb.server.admin.api.controller.team.request;

import ai.magicdb.server.tools.base.wrapper.request.PageQueryRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamPageCommonQueryRequest extends PageQueryRequest {
    /**
     * team id
     */
    @NotNull
    private Long teamId;

    /**
     * searchKey
     */
    private String searchKey;
}
