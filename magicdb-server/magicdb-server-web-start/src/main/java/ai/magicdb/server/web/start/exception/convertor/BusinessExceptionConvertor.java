package ai.magicdb.server.web.start.exception.convertor;

import ai.magicdb.server.tools.base.excption.BusinessException;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.common.util.I18nUtils;
import ai.magicdb.spi.util.ExceptionUtils;

/**
 * BusinessException
 *
 * @author Shi Yi
 */
public class BusinessExceptionConvertor implements ExceptionConvertor<BusinessException> {

    @Override
    public ActionResult convert(BusinessException exception) {
        return ActionResult.fail(exception.getCode(), I18nUtils.getMessage(exception.getCode(), exception.getArgs()),
            ExceptionUtils.getErrorInfoFromException(exception));
    }
}
