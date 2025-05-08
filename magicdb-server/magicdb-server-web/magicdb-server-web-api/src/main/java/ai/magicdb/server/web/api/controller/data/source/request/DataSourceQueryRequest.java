package ai.magicdb.server.web.api.controller.data.source.request;

import ai.magicdb.server.tools.base.wrapper.request.PageQueryRequest;
import lombok.Data;

/**
 * @author moji
 * @version ConnectionQueryRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceQueryRequest extends PageQueryRequest {

    /**
     * Alias fuzzy search terms
     */
    private String searchKey;
    /**
     * Connection Type
     *
     * @see ai.magicdb.server.domain.api.enums.DataSourceKindEnum
     */
    private String kind;
}
