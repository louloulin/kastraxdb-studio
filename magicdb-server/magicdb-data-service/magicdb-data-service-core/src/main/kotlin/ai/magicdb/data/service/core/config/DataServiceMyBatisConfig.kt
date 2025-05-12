package ai.magicdb.data.service.core.config

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Configuration

/**
 * 数据服务MyBatis配置
 *
 * @author magicdb
 */
@Configuration("dataServiceMyBatisConfig")
@MapperScan("ai.magicdb.dataservice.core.mapper")
class DataServiceMyBatisConfig
