-- 创建服务调用记录表
CREATE TABLE IF NOT EXISTS `data_service_call_record` (
    `id` VARCHAR(64) NOT NULL COMMENT '记录ID',
    `service_id` VARCHAR(64) NOT NULL COMMENT '服务ID',
    `service_name` VARCHAR(255) DEFAULT NULL COMMENT '服务名称',
    `call_time` TIMESTAMP NOT NULL COMMENT '调用时间',
    `execution_time` BIGINT NOT NULL COMMENT '执行时间（毫秒）',
    `success` TINYINT(1) NOT NULL COMMENT '是否成功',
    `error_message` VARCHAR(1000) DEFAULT NULL COMMENT '错误消息',
    `error_type` VARCHAR(100) DEFAULT NULL COMMENT '错误类型',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `client_ip` VARCHAR(50) DEFAULT NULL COMMENT '客户端IP',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数（JSON格式）',
    `response_data` TEXT DEFAULT NULL COMMENT '响应数据（JSON格式）',
    `method` VARCHAR(20) DEFAULT NULL COMMENT '调用方法',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '调用路径',
    `source` VARCHAR(50) DEFAULT NULL COMMENT '调用来源',
    PRIMARY KEY (`id`),
    INDEX `idx_service_call_record_service_id` (`service_id`),
    INDEX `idx_service_call_record_call_time` (`call_time`),
    INDEX `idx_service_call_record_success` (`success`),
    INDEX `idx_service_call_record_user_id` (`user_id`),
    INDEX `idx_service_call_record_error_type` (`error_type`)
) COMMENT='服务调用记录表';

-- 创建服务调用统计表（用于缓存统计结果）
CREATE TABLE IF NOT EXISTS `data_service_call_stats` (
    `id` VARCHAR(64) NOT NULL COMMENT '统计ID',
    `service_id` VARCHAR(64) NOT NULL COMMENT '服务ID',
    `service_name` VARCHAR(255) DEFAULT NULL COMMENT '服务名称',
    `stat_type` VARCHAR(50) NOT NULL COMMENT '统计类型',
    `stat_period` VARCHAR(20) NOT NULL COMMENT '统计周期（HOUR/DAY/WEEK/MONTH/ALL）',
    `start_time` TIMESTAMP NOT NULL COMMENT '开始时间',
    `end_time` TIMESTAMP NOT NULL COMMENT '结束时间',
    `total_calls` BIGINT NOT NULL DEFAULT 0 COMMENT '总调用次数',
    `success_calls` BIGINT NOT NULL DEFAULT 0 COMMENT '成功调用次数',
    `failed_calls` BIGINT NOT NULL DEFAULT 0 COMMENT '失败调用次数',
    `avg_execution_time` DOUBLE NOT NULL DEFAULT 0 COMMENT '平均执行时间（毫秒）',
    `max_execution_time` BIGINT NOT NULL DEFAULT 0 COMMENT '最大执行时间（毫秒）',
    `min_execution_time` BIGINT NOT NULL DEFAULT 0 COMMENT '最小执行时间（毫秒）',
    `unique_users` INT NOT NULL DEFAULT 0 COMMENT '调用用户数',
    `unique_ips` INT NOT NULL DEFAULT 0 COMMENT '调用IP数',
    `error_types` TEXT DEFAULT NULL COMMENT '错误类型统计（JSON格式）',
    `common_errors` TEXT DEFAULT NULL COMMENT '常见错误（JSON格式）',
    `calls_by_hour` TEXT DEFAULT NULL COMMENT '按小时统计的调用次数（JSON格式）',
    `calls_by_day` TEXT DEFAULT NULL COMMENT '按天统计的调用次数（JSON格式）',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_service_call_stats` (`service_id`, `stat_type`, `stat_period`, `start_time`, `end_time`),
    INDEX `idx_service_call_stats_service_id` (`service_id`),
    INDEX `idx_service_call_stats_update_time` (`update_time`)
) COMMENT='服务调用统计表';

-- 创建性能监控表（用于记录系统性能指标）
CREATE TABLE IF NOT EXISTS `data_service_performance` (
    `id` VARCHAR(64) NOT NULL COMMENT '记录ID',
    `service_id` VARCHAR(64) DEFAULT NULL COMMENT '服务ID，如果为NULL则表示系统整体',
    `service_name` VARCHAR(255) DEFAULT NULL COMMENT '服务名称',
    `record_time` TIMESTAMP NOT NULL COMMENT '记录时间',
    `memory_usage` DOUBLE NOT NULL DEFAULT 0 COMMENT '内存使用（MB）',
    `cpu_usage` DOUBLE NOT NULL DEFAULT 0 COMMENT 'CPU使用（%）',
    `thread_count` INT NOT NULL DEFAULT 0 COMMENT '线程数',
    `active_connections` INT NOT NULL DEFAULT 0 COMMENT '活跃连接数',
    `requests_per_second` DOUBLE NOT NULL DEFAULT 0 COMMENT '每秒请求数',
    `avg_response_time` DOUBLE NOT NULL DEFAULT 0 COMMENT '平均响应时间（毫秒）',
    `metadata` TEXT DEFAULT NULL COMMENT '其他元数据（JSON格式）',
    PRIMARY KEY (`id`),
    INDEX `idx_service_performance_service_id` (`service_id`),
    INDEX `idx_service_performance_record_time` (`record_time`)
) COMMENT='服务性能监控表';
