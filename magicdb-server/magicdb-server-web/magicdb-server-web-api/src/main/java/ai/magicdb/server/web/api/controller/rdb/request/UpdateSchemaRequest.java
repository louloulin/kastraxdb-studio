
package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author jipengfei
 * @version : UpdateSchemaRequest.java
 */
@Data
public class UpdateSchemaRequest extends DataSourceBaseRequest {

    private String newSchemaName;

}