/*
Navicat MySQL Data Transfer

Source Server         : localhost_3307
Source Server Version : 50549
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50549
File Encoding         : 65001

Date: 2019-06-22 11:24:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for daily
-- ----------------------------
DROP TABLE IF EXISTS `daily`;
CREATE TABLE `daily` (
  `Money` float(255,0) NOT NULL,
  `Date` date DEFAULT NULL,
  `Remark` varchar(255) DEFAULT NULL,
  `Type` int(11) DEFAULT NULL,
  `Category_id` int(11) DEFAULT NULL,
  `Asset_id` int(11) DEFAULT NULL,
  `Asset_name` varchar(255) DEFAULT NULL,
  `Category_name` varchar(255) DEFAULT NULL,
  `Id` int(11) DEFAULT NULL,
  `Category_imageId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of daily
-- ----------------------------
INSERT INTO `daily` VALUES ('300', '2019-06-22', '吃饭', '1', '1', '2', '微信', '餐饮', '4', '2131165304');
INSERT INTO `daily` VALUES ('5000', '2019-06-22', '电脑', '1', '8', '2', '微信', '电子产品', '2', '2131165303');
INSERT INTO `daily` VALUES ('100', '2019-05-31', '请开始您的记账', '2', '9', '2', '微信', '薪资', '1', '2131165309');
