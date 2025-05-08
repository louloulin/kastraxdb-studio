package ai.magicdb.server.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.spi.util.ExceptionUtils;

/**
 * Parameter exceptions currently include：
 * ConstraintViolationException
 * MissingServletRequestParameterException
 * IllegalArgumentException
 *
 * @author Shi Yi
 */
public class ParamExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail("common.paramError", exception.getMessage(), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
