package ai.magicdb.server.web.api.controller.data.source.request;

import java.util.List;

import ai.magicdb.spi.config.DriverConfig;
import jakarta.validation.constraints.NotNull;

import ai.magicdb.spi.model.KeyValue;
import ai.magicdb.spi.model.SSHInfo;
import ai.magicdb.spi.model.SSLInfo;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceUpdateRequest {

    /**
     * primary key id
     */
    @NotNull
    private Long id;

    /**
     * Connection alias
     */
    private String alias;

    /**
     * connection address
     */
    private String url;

    /**
     * Connect users
     */
    private String user;

    /**
     * password
     */
    private String password;

    /**
     * Connection Type
     */
    private String type;

    /**
     * environment type
     * @see EnvTypeEnum
     */
    private String envType;

    /**
     * environment id
     */
    private Integer environmentId;

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

    /**
     * service name
     */
    private String serviceName;

    /**
     * Service type
     */
    private String serviceType;
}
