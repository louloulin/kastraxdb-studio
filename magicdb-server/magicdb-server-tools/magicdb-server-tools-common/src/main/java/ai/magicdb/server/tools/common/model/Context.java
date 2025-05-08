package ai.magicdb.server.tools.common.model;

import java.io.Serial;
import java.io.Serializable;

import ai.magicdb.server.tools.base.constant.EasyToolsConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * contextual information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Context implements Serializable {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /***
     * User Info
     */
    private LoginUser loginUser;
}
