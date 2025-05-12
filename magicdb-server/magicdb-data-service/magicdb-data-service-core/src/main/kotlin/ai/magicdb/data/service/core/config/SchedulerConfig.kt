package ai.magicdb.data.service.core.config

import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.impl.StdSchedulerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Properties

/**
 * 调度器配置
 *
 * @author magicdb
 */
@Configuration
class SchedulerConfig {
    
    @Bean
    fun scheduler(): Scheduler {
        try {
            // 创建调度器属性
            val props = Properties()
            props.setProperty("org.quartz.scheduler.instanceName", "MagicDBScheduler")
            props.setProperty("org.quartz.scheduler.instanceId", "AUTO")
            props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool")
            props.setProperty("org.quartz.threadPool.threadCount", "10")
            props.setProperty("org.quartz.threadPool.threadPriority", "5")
            props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore")
            
            // 创建调度器工厂
            val schedulerFactory = StdSchedulerFactory(props)
            
            // 创建调度器
            return schedulerFactory.scheduler
        } catch (e: SchedulerException) {
            throw RuntimeException("创建调度器失败", e)
        }
    }
}
