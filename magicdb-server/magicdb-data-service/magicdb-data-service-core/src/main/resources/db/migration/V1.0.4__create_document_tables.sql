-- 创建服务文档表
CREATE TABLE IF NOT EXISTS `data_service_document` (
    `id` VARCHAR(64) NOT NULL COMMENT '文档ID',
    `title` VARCHAR(255) NOT NULL COMMENT '文档标题',
    `content` LONGTEXT NOT NULL COMMENT '文档内容',
    `format` VARCHAR(50) NOT NULL DEFAULT 'markdown' COMMENT '文档格式（如：markdown, html）',
    `service_id` VARCHAR(64) DEFAULT NULL COMMENT '服务ID',
    `group_id` VARCHAR(64) DEFAULT NULL COMMENT '分组ID',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `tags` VARCHAR(1000) DEFAULT NULL COMMENT '标签（JSON格式）',
    `is_public` TINYINT(1) DEFAULT 1 COMMENT '是否公开',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `metadata` TEXT DEFAULT NULL COMMENT '元数据（JSON格式）',
    PRIMARY KEY (`id`),
    INDEX `idx_service_id` (`service_id`),
    INDEX `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务文档表';

-- 创建文档模板表
CREATE TABLE IF NOT EXISTS `data_service_document_template` (
    `id` VARCHAR(64) NOT NULL COMMENT '模板ID',
    `name` VARCHAR(255) NOT NULL COMMENT '模板名称',
    `content` LONGTEXT NOT NULL COMMENT '模板内容',
    `format` VARCHAR(50) NOT NULL DEFAULT 'markdown' COMMENT '模板格式（如：markdown, html）',
    `type` VARCHAR(50) NOT NULL DEFAULT 'service' COMMENT '模板类型（如：service, group, api）',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统默认',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档模板表';
