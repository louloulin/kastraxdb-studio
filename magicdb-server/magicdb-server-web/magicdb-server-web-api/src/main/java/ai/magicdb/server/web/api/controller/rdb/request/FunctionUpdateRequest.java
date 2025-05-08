package ai.magicdb.server.web.api.controller.rdb.request;

import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Juechen
 * @version : FunctionUpdateRequest.java
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionUpdateRequest extends DataSourceBaseRequest {

    private String functionName;
    private String functionBody;

}
