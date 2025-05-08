
package ai.magicdb.server.web.api.controller.data.source.converter;

import ai.magicdb.spi.model.SSHInfo;
import ai.magicdb.server.web.api.controller.data.source.request.SSHTestRequest;

import org.mapstruct.Mapper;

/**
 * @author jipengfei
 * @version : SSHWebConverter.java
 */
@Mapper(componentModel = "spring")
public abstract class SSHWebConverter {

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract SSHInfo toInfo(SSHTestRequest request);
}