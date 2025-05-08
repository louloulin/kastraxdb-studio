package ai.magicdb.server.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;

import ai.magicdb.server.tools.common.util.I18nUtils;
import ai.magicdb.spi.util.ExceptionUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * MaxUploadSizeExceededException
 *
 * @author Shi Yi
 */
public class MaxUploadSizeExceededExceptionConvertor implements ExceptionConvertor<MaxUploadSizeExceededException> {

    @Override
    public ActionResult convert(MaxUploadSizeExceededException exception) {
        return ActionResult.fail("common.maxUploadSize", I18nUtils.getMessage("common.maxUploadSize"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
