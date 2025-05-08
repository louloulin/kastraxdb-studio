package ai.magicdb.server.web.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.spi.util.ExceptionUtils;
import org.springframework.validation.BindException;

/**
 * BindException
 *
 * @author Shi Yi
 */
public class BindExceptionConvertor implements ExceptionConvertor<BindException> {

    @Override
    public ActionResult convert(BindException exception) {
        String message = ExceptionConvertorUtils.buildMessage(exception.getBindingResult());
        return ActionResult.fail("common.paramError", message, ExceptionUtils.getErrorInfoFromException(exception));
    }
}
