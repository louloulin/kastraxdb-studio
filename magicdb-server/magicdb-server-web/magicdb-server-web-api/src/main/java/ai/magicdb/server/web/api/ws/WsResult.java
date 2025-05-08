package ai.magicdb.server.web.api.ws;


import ai.magicdb.server.tools.base.wrapper.Result;
import lombok.Data;

@Data
public class WsResult {
    /**
     * message id
     */
    private String uuid;

    /**
     * message content
     */
    private Result message;

    /**
     * message type
     */
    private String actionType;
}
