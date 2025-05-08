package ai.magicdb.server.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;

import ai.magicdb.server.tools.common.util.I18nUtils;
import ai.magicdb.spi.util.ExceptionUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * MethodArgumentTypeMismatchException
 *
 * @author Shi Yi
 */
public class MethodArgumentTypeMismatchExceptionConvertor
    implements ExceptionConvertor<MethodArgumentTypeMismatchException> {

    @Override
    public ActionResult convert(MethodArgumentTypeMismatchException exception) {
        return ActionResult.fail("common.paramError", I18nUtils.getMessage("common.paramError"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
