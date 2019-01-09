
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `account` varchar(128) DEFAULT NULL COMMENT '用户账号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(24) DEFAULT NULL COMMENT '手机号',
  `status` INT DEFAULT 0 COMMENT '状态 0 - 有效 1 - 无效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_uk` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';