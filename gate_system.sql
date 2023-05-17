/*
 Navicat MySQL Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 50724 (5.7.24)
 Source Host           : localhost:3306
 Source Schema         : gate_system

 Target Server Type    : MySQL
 Target Server Version : 50724 (5.7.24)
 File Encoding         : 65001

 Date: 17/05/2023 15:12:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_authority
-- ----------------------------
DROP TABLE IF EXISTS `admin_authority`;
CREATE TABLE `admin_authority`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `device_id` bigint(20) NOT NULL COMMENT '设备id',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_authority
-- ----------------------------
INSERT INTO `admin_authority` VALUES (1619266035321233410, 1619259262975684609, 1, 1, '2023-01-28 17:28:02', 1, '2023-01-28 17:28:02');
INSERT INTO `admin_authority` VALUES (1619303522236215297, 1619259262975684609, 3, 1, '2023-01-28 19:57:00', 1, '2023-01-28 19:57:00');
INSERT INTO `admin_authority` VALUES (1625458617067855874, 1619259262975684609, 2, 1, '2023-02-14 19:35:09', 1, '2023-02-14 19:35:09');
INSERT INTO `admin_authority` VALUES (1628563389899665409, 1618947469107613697, 4, 1, '2023-02-23 09:12:24', 1, '2023-02-23 09:12:24');

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
  `ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备ip',
  `status` int(8) NOT NULL COMMENT '设备状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES (1, '101', '192.168.1.1', 2, NULL);
INSERT INTO `device` VALUES (2, '102', '192.168.1.2', 2, NULL);
INSERT INTO `device` VALUES (3, '201', '192.168.1.3', 2, NULL);
INSERT INTO `device` VALUES (4, '202', '192.168.1.4', 2, NULL);
INSERT INTO `device` VALUES (1622607320111198209, '新设备', '127.0.0.1', 2, '2023-02-06 22:45:06');
INSERT INTO `device` VALUES (1622842243506057218, '新设备', '192.0.0.6', 2, '2023-02-07 14:18:36');
INSERT INTO `device` VALUES (1633030716397543426, '曾经使用过的设备', '10.73.174.186', 2, NULL);

-- ----------------------------
-- Table structure for label
-- ----------------------------
DROP TABLE IF EXISTS `label`;
CREATE TABLE `label`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `label_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标签名称',
  `label_color` int(11) NOT NULL COMMENT '标签颜色',
  `create_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of label
-- ----------------------------

-- ----------------------------
-- Table structure for permission_records
-- ----------------------------
DROP TABLE IF EXISTS `permission_records`;
CREATE TABLE `permission_records`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `device_id` bigint(20) NOT NULL COMMENT '设备id',
  `card_id` bigint(20) NULL DEFAULT NULL COMMENT '校园卡id',
  `permission_time` datetime NOT NULL COMMENT '通行时间',
  `is_success` int(11) NOT NULL COMMENT '是否通行成功',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '通行记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission_records
-- ----------------------------
INSERT INTO `permission_records` VALUES (1618983078583524241, 1618947348009668610, 1, NULL, '2023-01-29 09:21:55', 1);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户姓名',
  `account` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户学号/工号',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `card_Id` bigint(20) NULL DEFAULT NULL COMMENT '校园卡id;',
  `phone_number` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `type` int(8) NOT NULL COMMENT '用户类型',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_pk`(`account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '默认超级管理员', '123456', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, 3, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (1618947348009668610, '李四', '222019333210046', '', NULL, '', 1, 1, '2023-01-27 20:21:41', 1, '2023-03-02 21:06:17');
INSERT INTO `user` VALUES (1618947406453100546, '张三', '222019333210045', '', NULL, '', 1, 1, '2023-01-27 20:21:55', 1, '2023-03-02 21:08:20');
INSERT INTO `user` VALUES (1618947469107613697, '王五', '222019333210122', '96e79218965eb72c92a549dd5a330112', NULL, '', 2, 1, '2023-01-27 20:22:10', 1, '2023-02-23 09:45:40');
INSERT INTO `user` VALUES (1619259262975684609, '测试管理员', '123455', '00c66aaf5f2c3f49946f15c1ad2ea0d3', NULL, '', 2, 1, '2023-01-28 17:01:07', 1, '2023-01-28 17:01:07');
INSERT INTO `user` VALUES (1629361434727813122, '测试用户', '123444', 'e10adc3949ba59abbe56e057f20f883e', 1090009343, NULL, 1, 1, '2023-02-25 14:03:33', 1, '2023-03-07 16:55:28');

-- ----------------------------
-- Table structure for user_apply
-- ----------------------------
DROP TABLE IF EXISTS `user_apply`;
CREATE TABLE `user_apply`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `device_id` bigint(20) NOT NULL COMMENT '设备id',
  `begin_time` datetime NOT NULL COMMENT '起始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_apply
-- ----------------------------
INSERT INTO `user_apply` VALUES (1618949458642161665, 1618947406453100546, 2, '2023-01-27 20:21:55', '2023-01-28 20:21:55', '2023-01-27 20:30:04');
INSERT INTO `user_apply` VALUES (1618949509879779330, 1618947348009668610, 2, '2023-01-27 20:21:55', '2023-01-28 20:21:55', '2023-01-27 20:30:16');

-- ----------------------------
-- Table structure for user_label
-- ----------------------------
DROP TABLE IF EXISTS `user_label`;
CREATE TABLE `user_label`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `label_id` bigint(20) NOT NULL COMMENT '标签id',
  `create_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户拥有的标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_label
-- ----------------------------

-- ----------------------------
-- Table structure for user_permit
-- ----------------------------
DROP TABLE IF EXISTS `user_permit`;
CREATE TABLE `user_permit`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `device_id` bigint(20) NOT NULL COMMENT '设备id',
  `begin_time` datetime NOT NULL COMMENT '起始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户通行' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_permit
-- ----------------------------
INSERT INTO `user_permit` VALUES (1618983018765836290, 1618947348009668610, 1, '2023-01-27 20:21:55', '2023-01-29 21:21:55', 1, '2023-01-27 22:43:26', 1, '2023-01-27 22:43:26');
INSERT INTO `user_permit` VALUES (1618983062650839041, 1618947469107613697, 1, '2023-01-27 20:21:55', '2023-01-29 21:21:55', 1, '2023-01-27 22:43:36', 1, '2023-01-27 22:43:36');
INSERT INTO `user_permit` VALUES (1618983078589194241, 1618947469107613697, 4, '2023-01-27 20:21:55', '2023-01-29 21:21:55', 1, '2023-01-27 22:43:40', 1, '2023-01-27 22:43:40');
INSERT INTO `user_permit` VALUES (1619320928924938241, 1618947406453100546, 1, '2023-01-27 20:21:55', '2023-01-28 20:21:55', 1619259262975684609, '2023-01-28 21:06:10', 1619259262975684609, '2023-01-28 21:06:10');
INSERT INTO `user_permit` VALUES (1623913432823410690, 1618947469107613697, 4, '2023-01-27 20:21:55', '2023-01-28 20:21:55', 1, '2023-02-10 13:15:08', 1, '2023-02-10 13:15:08');
INSERT INTO `user_permit` VALUES (1625472525203718146, 1618947469107613697, 2, '2023-02-15 00:00:00', '2023-02-18 00:00:00', 1, '2023-02-14 20:30:24', 1, '2023-02-14 20:30:24');
INSERT INTO `user_permit` VALUES (1625472525203718147, 1618947406453100546, 2, '2023-02-15 00:00:00', '2023-02-18 00:00:00', 1, '2023-02-14 20:30:24', 1, '2023-02-14 20:30:24');
INSERT INTO `user_permit` VALUES (1629367667216162817, 1629361434727813122, 2, '2023-02-27 00:00:00', '2023-03-06 00:00:00', 1619259262975684609, '2023-02-25 14:28:19', 1619259262975684609, '2023-02-25 14:28:19');
INSERT INTO `user_permit` VALUES (1633660245679853570, 1629361434727813122, 3, '2023-03-08 00:00:00', '2023-03-10 00:00:00', 1, '2023-03-09 10:45:29', 1, '2023-03-09 10:45:29');
INSERT INTO `user_permit` VALUES (1658723314025967617, 1629361434727813122, 1, '2023-05-17 00:00:00', '2023-05-19 00:00:00', 1, '2023-05-17 14:37:10', 1, '2023-05-17 14:37:10');

SET FOREIGN_KEY_CHECKS = 1;
