/*
 Navicat Premium Data Transfer

 Source Server         : pms.bizmsg.net
 Source Server Type    : MySQL
 Source Server Version : 50560
 Source Host           : pms.bizmsg.net
 Source Database       : jbuscloud

 Target Server Type    : MySQL
 Target Server Version : 50560
 File Encoding         : utf-8

 Date: 07/26/2018 15:06:09 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `t_cmd_encode`
-- ----------------------------
DROP TABLE IF EXISTS `t_cmd_encode`;
CREATE TABLE `t_cmd_encode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sensorId` int(11) NOT NULL,
  `cmdNo` int(11) NOT NULL,
  `cmdName` varchar(255) NOT NULL,
  `scriptText` text NOT NULL,
  `paramSchema` varchar(255) NOT NULL,
  `includeCrc` tinyint(1) NOT NULL DEFAULT '0' COMMENT '脚本处理是否包含crc校验。0:不包含，1:包含',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_dat_decode`
-- ----------------------------
DROP TABLE IF EXISTS `t_dat_decode`;
CREATE TABLE `t_dat_decode` (
  `id` int(11) NOT NULL,
  `deviceId` int(11) NOT NULL,
  `scriptText` text NOT NULL,
  `resultSchema` varchar(255) NOT NULL,
  `includeCrc` tinyint(1) NOT NULL COMMENT '0:不包含，1:包含',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_device`
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` varchar(255) NOT NULL,
  `deviceName` varchar(255) NOT NULL,
  `longitude` varchar(25) DEFAULT NULL,
  `latitude` varchar(25) DEFAULT NULL,
  `crcMode` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1: crc16_modbus',
  `owerId` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_privilege`
-- ----------------------------
DROP TABLE IF EXISTS `t_privilege`;
CREATE TABLE `t_privilege` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `deviceId` int(11) NOT NULL,
  `privilege` int(11) NOT NULL COMMENT '配置|控制|查看',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_pwd_reset`
-- ----------------------------
DROP TABLE IF EXISTS `t_pwd_reset`;
CREATE TABLE `t_pwd_reset` (
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `newPassword` varchar(255) DEFAULT NULL,
  `newEmail` varchar(255) DEFAULT NULL,
  `confirmCode` varchar(255) NOT NULL,
  `status` tinyint(1) NOT NULL COMMENT '1:待验证，2:已验证',
  `accessIp` varchar(25) NOT NULL,
  `accessTime` datetime NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_sensor`
-- ----------------------------
DROP TABLE IF EXISTS `t_sensor`;
CREATE TABLE `t_sensor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NOT NULL,
  `sensorNo` int(11) NOT NULL,
  `sensorName` varchar(25) NOT NULL,
  `memo` varchar(255) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `secretId` varchar(255) NOT NULL,
  `secretKey` varchar(255) NOT NULL,
  `nickName` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `status` tinyint(1) NOT NULL COMMENT '1:正常，9:停用',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`) USING BTREE,
  UNIQUE KEY `secretId` (`secretId`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `t_user_reg`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_reg`;
CREATE TABLE `t_user_reg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickName` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `confirmCode` varchar(255) NOT NULL,
  `status` tinyint(1) NOT NULL COMMENT '1:待验证，2:已验证',
  `accessIp` varchar(25) NOT NULL,
  `accessTime` datetime NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
