package ai.magicdb.server.domain.api.service;

import java.util.List;

import ai.magicdb.server.domain.api.model.DataSource;
import ai.magicdb.server.domain.api.param.datasource.DataSourceCreateParam;
import ai.magicdb.server.domain.api.param.datasource.DataSourcePageQueryParam;
import ai.magicdb.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.magicdb.server.domain.api.param.datasource.DataSourceSelector;
import ai.magicdb.server.domain.api.param.datasource.DataSourceUpdateParam;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.server.tools.base.wrapper.result.PageResult;
import ai.magicdb.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.magicdb.spi.model.Database;
import jakarta.validation.constraints.NotNull;

/**
 * Data source management services
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 September 23, 2022 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface DataSourceService {

    /**
     * Create data source connection
     *
     * @param param
     * @return
     */
    DataResult<Long> createWithPermission(DataSourceCreateParam param);

    /**
     * Update data source connection
     *
     * @param param
     * @return
     */
    DataResult<Long> updateWithPermission(DataSourceUpdateParam param);

    /**
     * Delete data source connection
     *
     * @param id
     * @return
     */
    ActionResult deleteWithPermission(@NotNull Long id);

    /**
     * Query data source connection details based on id
     *
     * @param id
     * @return
     */
    DataResult<DataSource> queryById(@NotNull Long id);

    /**
     * Query data source connection details based on id
     *
     * @param id
     * @return
     * @throws ai.magicdb.server.tools.common.exception.DataNotFoundException
     */
    DataResult<DataSource> queryExistent(@NotNull Long id, DataSourceSelector selector);

    /**
     * clone connection
     *
     * @param id
     * @return
     */
    DataResult<Long> copyByIdWithPermission(@NotNull Long id);

    /**
     * Paginated query data source list
     *
     * @param param
     * @param selector
     * @return
     */
    PageResult<DataSource> queryPage(DataSourcePageQueryParam param, DataSourceSelector selector);

    /**
     * Paginated query data source list
     * Need to determine permissions
     *
     * @param param
     * @param selector
     * @return
     * @throws PermissionDeniedBusinessException
     */
    PageResult<DataSource> queryPageWithPermission(DataSourcePageQueryParam param, DataSourceSelector selector);

    /**
     * Query data source by ID list
     *
     * @param ids
     * @return
     * @deprecated Use {@link #listQuery(List, DataSourceSelector)}
     */
    ListResult<DataSource> queryByIds(List<Long> ids);

    /**
     * Query data source by ID list
     *
     * @param idList
     * @return
     */
    ListResult<DataSource> listQuery(List<Long> idList, DataSourceSelector selector);

    /**
     * Data source connection test
     *
     * @param param
     * @return
     */
    ActionResult preConnect(DataSourcePreConnectParam param);

    /**
     * Connect to data source
     *
     * @param id
     * @return
     */
    ListResult<Database> connect(Long id);

    /**
     * Close data source connection
     *
     * @param id
     * @return
     */
    ActionResult close(Long id);

}
