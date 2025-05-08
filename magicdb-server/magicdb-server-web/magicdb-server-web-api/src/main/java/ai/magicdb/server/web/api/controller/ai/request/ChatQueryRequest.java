package ai.magicdb.server.web.api.controller.ai.request;

import java.util.List;

import ai.magicdb.server.web.api.controller.ai.enums.PromptType;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * Chat query input parameters
 *
 * @author moji
 * @version ChatQueryRequest.java, v 0.1 April 2, 2023 13:28 moji Exp $
 * @date 2023/04/02
 */
@Data
public class ChatQueryRequest extends DataSourceBaseRequest {

    /**
     * Enter message
     */
    private String message;

    /**
     * SQL function type
     * @see PromptType
     */
    private String promptType;

    /**
     * table name list
     */
    private List<String> tableNames;

    /**
     * Target SQL data type
     * @see ai.magicdb.server.domain.support.enums.DbTypeEnum
     */
    private String destSqlType;

    /**
     * More remarks: such as requirements or restrictions, etc.
     */
    private String ext;
}
