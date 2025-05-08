package ai.magicdb.server.domain.api.service;

import ai.magicdb.server.domain.api.model.DataSource;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import jakarta.validation.constraints.NotNull;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
public interface DataSourceAccessBusinessService {
    /**
     * delete
     *
     * @param dataSource
     * @return
     */
    ActionResult checkPermission(@NotNull DataSource dataSource);
}
