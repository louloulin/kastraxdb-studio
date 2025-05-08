
package ai.magicdb.server.admin.api.controller.common.request;

import ai.magicdb.server.domain.api.enums.AccessObjectTypeEnum;
import ai.magicdb.server.tools.base.wrapper.request.PageQueryRequest;
import lombok.Data;

/**
 * Common pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUserPageQueryRequest extends PageQueryRequest {

    /**
     * Authorization type
     *
     * @see AccessObjectTypeEnum
     */
    private String type;

    /**
     * searchKey
     */
    private String searchKey;
}
