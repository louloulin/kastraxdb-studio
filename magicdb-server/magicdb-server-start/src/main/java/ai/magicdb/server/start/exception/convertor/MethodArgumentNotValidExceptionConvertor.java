package ai.magicdb.server.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;

import ai.magicdb.spi.util.ExceptionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * MethodArgumentNotValidException
 *
 * @author Shi Yi
 */
public class MethodArgumentNotValidExceptionConvertor implements ExceptionConvertor<MethodArgumentNotValidException> {

    @Override
    public ActionResult convert(MethodArgumentNotValidException exception) {
        String message = ExceptionConvertorUtils.buildMessage(exception.getBindingResult());
        return ActionResult.fail("common.paramError", message, ExceptionUtils.getErrorInfoFromException(exception));
    }
}
