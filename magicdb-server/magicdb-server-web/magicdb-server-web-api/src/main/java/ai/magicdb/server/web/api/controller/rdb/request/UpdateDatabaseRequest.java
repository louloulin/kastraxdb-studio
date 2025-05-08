
package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author jipengfei
 * @version : UpdateDatasourceRequest.java
 */
@Data
public class UpdateDatabaseRequest extends DataSourceBaseRequest {

    private String newDatabaseName;
}