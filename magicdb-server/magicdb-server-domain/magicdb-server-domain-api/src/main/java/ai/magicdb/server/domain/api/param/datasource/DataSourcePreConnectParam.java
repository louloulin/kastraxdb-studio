package ai.magicdb.server.domain.api.param.datasource;

import java.util.List;

import ai.magicdb.spi.config.DriverConfig;
import ai.magicdb.spi.model.KeyValue;
import ai.magicdb.spi.model.SSHInfo;
import ai.magicdb.spi.model.SSLInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourcePreConnectParam {

    /**
     * Connection alias
     */
    private String alias;

    /**
     * connection address
     */
    @NotNull
    private String url;

    /**
     * Connect users
     */
    private String user;

    /**
     * password
     */
    @NotNull
    private String password;

    /**
     * Connection Type
     */
    @NotNull
    private String type;


    /**
     * host
     */
    private String host;

    /**
     * port
     */
    private String port;

    /**
     * ssh
     */
    private SSHInfo ssh;

    /**
     * ssh
     */
    private SSLInfo ssl;

    /**
     * sid
     */
    private String sid;

    /**
     * driver
     */
    private String driver;


    /**
     * jdbc version
     */
    private String jdbc;

    /**
     * Extended Information
     */
    private List<KeyValue> extendInfo;

    /**
     * Driver configuration
     */
    private DriverConfig driverConfig;
}
