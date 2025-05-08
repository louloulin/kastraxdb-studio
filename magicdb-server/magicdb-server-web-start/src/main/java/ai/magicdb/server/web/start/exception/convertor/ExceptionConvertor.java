package ai.magicdb.server.web.start.exception.convertor;

import ai.magicdb.server.tools.base.wrapper.result.ActionResult;

/**
 * exception converter
 *
 * @author Shi Yi
 */
public interface ExceptionConvertor<T extends Throwable> {

    /**
     * Conversion exception
     *
     * @param exception
     * @return
     */
    ActionResult convert(T exception);
}
