
package ai.magicdb.server.admin.api.controller.datasource.vo;

import ai.magicdb.server.common.api.controller.vo.SimpleEnvironmentVO;
import lombok.Data;

/**
 * Data Source
 *
 * @author Jiaju Zhuang
 */
@Data
public class SimpleDataSourceVO {

    /**
     * primary key id
     */
    private Long id;

    /**
     * Connection alias
     */
    private String alias;

    /**
     * connection address
     */
    private String url;

    /**
     * environment id
     */
    private Long environmentId;

    /**
     * environment
     */
    private SimpleEnvironmentVO environment;
}
