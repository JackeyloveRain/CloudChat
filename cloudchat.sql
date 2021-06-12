/*
Navicat MySQL Data Transfer

Source Server         : first_link
Source Server Version : 80025
Source Host           : localhost:3306
Source Database       : cloudchat

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2021-06-12 21:43:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for chat_room
-- ----------------------------
DROP TABLE IF EXISTS `chat_room`;
CREATE TABLE `chat_room` (
  `id_room` int NOT NULL AUTO_INCREMENT,
  `name_room` varchar(255) NOT NULL,
  `pwd_room` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `id_master` varchar(255) NOT NULL,
  `description_room` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `num_room` int DEFAULT NULL,
  PRIMARY KEY (`id_room`),
  UNIQUE KEY `name_room` (`name_room`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of chat_room
-- ----------------------------

-- ----------------------------
-- Table structure for chat_room_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_room_message`;
CREATE TABLE `chat_room_message` (
  `id_chat_room_message` int NOT NULL AUTO_INCREMENT,
  `name_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name_room` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content_msg` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `time_msg` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_chat_room_message`)
) ENGINE=InnoDB AUTO_INCREMENT=664 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Table structure for chat_room_user
-- ----------------------------
DROP TABLE IF EXISTS `chat_room_user`;
CREATE TABLE `chat_room_user` (
  `id_chat_room_user` int NOT NULL AUTO_INCREMENT,
  `name_room` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name_user` varchar(255) NOT NULL,
  `add_time` datetime NOT NULL,
  PRIMARY KEY (`id_chat_room_user`)
) ENGINE=InnoDB AUTO_INCREMENT=295 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of chat_room_user
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `name_user` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `pwd_user` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `email_user` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `telephone_user` varchar(20) DEFAULT NULL,
  `birthday_user` date DEFAULT NULL,
  `sex_user` tinyint(1) DEFAULT NULL,
  `isban_user` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email_user` (`email_user`) USING BTREE,
  UNIQUE KEY `name_user` (`name_user`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', 'z', '1', 'zzz@163.com', null, null, null, '0');
INSERT INTO `user_info` VALUES ('5', 'ashen', '123', 'ashen@163.com', null, null, null, '0');
INSERT INTO `user_info` VALUES ('8', 'rr', '1', 'rui@163.com', null, null, null, '0');
INSERT INTO `user_info` VALUES ('13', 'rrr', '12345678', '1111@111', null, null, null, '0');
INSERT INTO `user_info` VALUES ('14', '11111111111', '123434311', '1113771121@qq.com', null, null, null, '0');
INSERT INTO `user_info` VALUES ('15', 'Zuo_zuo', '11111111', 'wq@13', null, null, null, '0');
