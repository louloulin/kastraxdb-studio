package ai.magicdb.server.tools.common.exception;

import java.io.Serial;

import ai.magicdb.server.tools.base.constant.EasyToolsConstant;
import ai.magicdb.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * User login exception
 *
 * @author Jiaju Zhuang
 */
@Getter
public class NeedLoggedInBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public NeedLoggedInBusinessException() {
        super("common.needLoggedIn");
    }
}