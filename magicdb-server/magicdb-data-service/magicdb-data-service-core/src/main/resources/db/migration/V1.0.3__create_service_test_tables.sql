-- 创建服务测试表
CREATE TABLE IF NOT EXISTS `data_service_test` (
    `id` VARCHAR(64) NOT NULL COMMENT '测试ID',
    `name` VARCHAR(255) NOT NULL COMMENT '测试名称',
    `service_id` VARCHAR(64) NOT NULL COMMENT '服务ID',
    `parameters` TEXT COMMENT '测试参数（JSON格式）',
    `expected_result` TEXT COMMENT '预期结果（JSON格式）',
    `description` VARCHAR(1000) COMMENT '测试描述',
    `create_time` BIGINT COMMENT '创建时间',
    `update_time` BIGINT COMMENT '更新时间',
    `create_user_id` BIGINT COMMENT '创建用户ID',
    `tags` VARCHAR(1000) COMMENT '标签（JSON格式）',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `sort` INT DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`id`),
    INDEX `idx_service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务测试表';

-- 创建服务测试结果表
CREATE TABLE IF NOT EXISTS `data_service_test_result` (
    `test_id` VARCHAR(64) NOT NULL COMMENT '测试ID',
    `test_name` VARCHAR(255) COMMENT '测试名称',
    `service_id` VARCHAR(64) NOT NULL COMMENT '服务ID',
    `parameters` TEXT COMMENT '测试参数（JSON格式）',
    `expected_result` TEXT COMMENT '预期结果（JSON格式）',
    `actual_result` TEXT COMMENT '实际结果（JSON格式）',
    `passed` TINYINT(1) DEFAULT 0 COMMENT '是否通过',
    `error_message` VARCHAR(1000) COMMENT '错误信息',
    `execution_time` BIGINT COMMENT '执行时间（毫秒）',
    `execution_timestamp` BIGINT COMMENT '执行时间戳',
    `execution_user_id` BIGINT COMMENT '执行用户ID',
    PRIMARY KEY (`test_id`),
    INDEX `idx_service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务测试结果表';
