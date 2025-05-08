package ai.magicdb.server.domain.core.converter;

import ai.magicdb.server.domain.repository.entity.JdbcDriverDO;
import ai.magicdb.spi.config.DriverConfig;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class DriverConfigConverter {
    public abstract DriverConfig do2Config(JdbcDriverDO driverDO);

}
