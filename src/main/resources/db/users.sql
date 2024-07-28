SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
     `id` bigint AUTO_INCREMENT NOT NULL COMMENT '主键',
     `name` varchar(20) NOT NULL COMMENT '真实姓名',
     `password` varchar(255) NOT NULL COMMENT '加密密码',
     `official_email` varchar(30) NOT NULL COMMENT '南大邮箱',
     `official_number` varchar(30) NOT NULL COMMENT '南大学号',
     `role` int NOT NULL COMMENT '用户角色',
     `user_avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
     `create_time` datetime NOT NULL COMMENT '创建时间',
     `last_login_time` datetime DEFAULT NULL COMMENT '上次登陆时间',
     `english_name` varchar(30) DEFAULT NULL COMMENT '英文名',
     `gender` int DEFAULT NULL COMMENT '性别',
     `birthday` datetime DEFAULT NULL COMMENT '生日',
     `phone` char(11) DEFAULT NULL COMMENT '手机号',
     `content_email` varchar(30) DEFAULT NULL COMMENT '联系邮箱',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;


