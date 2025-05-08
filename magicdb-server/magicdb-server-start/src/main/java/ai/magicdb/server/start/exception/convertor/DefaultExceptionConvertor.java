package ai.magicdb.server.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.common.util.I18nUtils;
import ai.magicdb.spi.util.ExceptionUtils;

/**
 * Default exception handling
 * Throw system exception directly
 *
 * @author Shi Yi
 */
public class DefaultExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail("common.systemError", I18nUtils.getMessage("common.systemError"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
