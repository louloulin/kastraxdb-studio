package ai.magicdb.server.domain.api.service;

import ai.magicdb.server.domain.api.param.PinTableParam;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;

import java.util.List;

public interface PinService {

    /**
     * User pin table
     * @param param
     * @return
     */
    ActionResult pinTable(PinTableParam param);


    /**
     * Delete pin table
     * @param param
     * @return
     */
    ActionResult deletePinTable(PinTableParam param);


    /**
     * Query user pin tables
     * @param param
     * @return
     */
    ListResult<String> queryPinTables(PinTableParam param);
}
