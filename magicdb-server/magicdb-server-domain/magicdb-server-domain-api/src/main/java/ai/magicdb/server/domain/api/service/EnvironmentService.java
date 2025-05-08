package ai.magicdb.server.domain.api.service;

import java.util.List;

import ai.magicdb.server.domain.api.model.Environment;
import ai.magicdb.server.domain.api.param.EnvironmentPageQueryParam;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.server.tools.base.wrapper.result.PageResult;

/**
 * environment
 *
 * @author Jiaju Zhuang
 */
public interface EnvironmentService {

    /**
     * List Query Data
     *
     * @param idList
     * @return
     */
    ListResult<Environment> listQuery(List<Long> idList);

    /**
     * Paging Query Data
     *
     * @param param
     * @return
     */
    PageResult<Environment> pageQuery(EnvironmentPageQueryParam param);

}
