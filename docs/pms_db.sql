/*
 Navicat Premium Data Transfer

 Source Server         : dasanxia
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : pms_db

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 13/04/2026 17:58:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `cust_id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增，顾客唯一编号',
  `cust_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '顾客姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话（登录/查询用）',
  `is_member` tinyint NOT NULL DEFAULT 0 COMMENT '1=会员, 0=非会员',
  `member_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员等级：普通/银卡/金卡/铂金等',
  `total_consume` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '累计消费总金额，默认0',
  `birthday` date NULL DEFAULT NULL COMMENT '生日（会员营销用）',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `status` tinyint NULL DEFAULT 1 COMMENT '1=正常, 0=拉黑/禁用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`cust_id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE COMMENT '手机号索引，用于查询'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '顾客/会员信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, '王大伯', '13566667777', 1, '金卡', 1200.50, '1965-05-12', NULL, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `customer` VALUES (2, '刘女士', '13788889999', 1, '普通', 85.00, '1992-10-24', NULL, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');

-- ----------------------------
-- Table structure for medicine
-- ----------------------------
DROP TABLE IF EXISTS `medicine`;
CREATE TABLE `medicine`  (
  `med_id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增，药品唯一编号',
  `med_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品通用名称',
  `med_alias` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名/别名（可选）',
  `med_type` enum('中药','西药','器械') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：中药/西药/器械',
  `spec` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格（如：0.5g*12片、10ml）',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计量单位（盒/瓶/支/袋/片等）',
  `dosage_form` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '剂型（片剂/胶囊/颗粒/针剂/膏剂等）',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `approval_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批准文号（国药准字/械备号等）',
  `retail_price` decimal(10, 2) NOT NULL COMMENT '零售售价',
  `purchase_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考进价',
  `is_rx` tinyint NULL DEFAULT 0 COMMENT '1=处方药, 0=非处方药',
  `stock_min` int NULL DEFAULT 0 COMMENT '库存预警下限',
  `status` tinyint NULL DEFAULT 1 COMMENT '1=正常在售, 0=停售/下架（逻辑删除标识）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注（禁忌、注意事项等）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`med_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药品基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine
-- ----------------------------
INSERT INTO `medicine` VALUES (1, '感冒灵颗粒', NULL, '西药', '10g*9袋', '盒', '颗粒', NULL, NULL, 15.50, 8.20, 0, 20, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `medicine` VALUES (2, '安宫牛黄丸', NULL, '中药', '3g*1丸', '盒', '丸剂', NULL, NULL, 580.00, 420.00, 0, 5, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `medicine` VALUES (3, '阿莫西林胶囊', NULL, '西药', '0.25g*24粒', '盒', '胶囊', NULL, NULL, 12.00, 5.50, 1, 50, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');

-- ----------------------------
-- Table structure for purchase_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_item`;
CREATE TABLE `purchase_item`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，自增，明细唯一ID',
  `purchase_id` bigint NOT NULL COMMENT '关联采购主表 purchase_order.purchase_id',
  `med_id` int NOT NULL COMMENT '关联药品表 medicine.med_id',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品批号，用于入库关联',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `expire_date` date NULL DEFAULT NULL COMMENT '有效期至，效期管理依据',
  `purchase_num` int NOT NULL COMMENT '采购数量',
  `purchase_price` decimal(10, 2) NOT NULL COMMENT '药品进价（单价）',
  `total_price` decimal(10, 2) NOT NULL COMMENT '该药品小计金额',
  `cabinet` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '建议存放药柜：中药柜/西药柜/针剂柜',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `fk_purchase_item_order`(`purchase_id` ASC) USING BTREE,
  INDEX `fk_purchase_item_med`(`med_id` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_item_med` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_purchase_item_order` FOREIGN KEY (`purchase_id`) REFERENCES `purchase_order` (`purchase_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_item
-- ----------------------------

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order`  (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，采购单号，自增',
  `supplier_id` int NOT NULL COMMENT '关联supplier.supplier_id 供应商ID',
  `user_id` int NOT NULL COMMENT '关联user.user_id 制单/采购操作员',
  `purchase_time` datetime NOT NULL COMMENT '采购入库时间',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '采购总金额',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：现金/转账/欠款',
  `purchase_status` tinyint NULL DEFAULT 0 COMMENT '1=已入库, 0=待入库, 2=已作废',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`purchase_id`) USING BTREE,
  INDEX `fk_purchase_supplier`(`supplier_id` ASC) USING BTREE,
  INDEX `fk_purchase_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_purchase_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_order
-- ----------------------------

-- ----------------------------
-- Table structure for sale_item
-- ----------------------------
DROP TABLE IF EXISTS `sale_item`;
CREATE TABLE `sale_item`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，自增，明细唯一ID',
  `order_id` bigint NOT NULL COMMENT '关联sale_order.order_id',
  `med_id` int NOT NULL COMMENT '关联medicine.med_id 药品ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品批号，关联库存表',
  `quantity` int NOT NULL COMMENT '销售数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '销售单价',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `fk_item_order`(`order_id` ASC) USING BTREE,
  INDEX `fk_item_medicine`(`med_id` ASC) USING BTREE,
  CONSTRAINT `fk_item_medicine` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `sale_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale_item
-- ----------------------------
INSERT INTO `sale_item` VALUES (1, 202604130001, 1, '20250101A', 2, 15.50, 31.00, '2026-04-13 17:56:51');

-- ----------------------------
-- Table structure for sale_order
-- ----------------------------
DROP TABLE IF EXISTS `sale_order`;
CREATE TABLE `sale_order`  (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，订单号（支持时间戳生成）',
  `cust_id` int NULL DEFAULT NULL COMMENT '关联顾客表customer.cust_id，顾客ID',
  `user_id` int NOT NULL COMMENT '关联用户表user.user_id，操作员/店员',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `total_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：现金/微信/支付宝/医保/刷卡',
  `order_type` tinyint NULL DEFAULT 1 COMMENT '1=线下, 0=线上',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '0=未支付, 1=已支付, 2=已退款',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注（折扣、赠送、特殊说明）',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `fk_sale_customer`(`cust_id` ASC) USING BTREE,
  INDEX `fk_sale_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_sale_customer` FOREIGN KEY (`cust_id`) REFERENCES `customer` (`cust_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sale_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 202604130002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale_order
-- ----------------------------
INSERT INTO `sale_order` VALUES (202604130001, 1, 2, '2026-04-13 17:56:51', 31.00, '微信', 1, 1, NULL, '2026-04-13 17:56:51');

-- ----------------------------
-- Table structure for stock
-- ----------------------------
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock`  (
  `stock_id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增，库存记录唯一ID',
  `med_id` int NOT NULL COMMENT '关联药品信息表med_id',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品批号（核心关联字段）',
  `expire_date` date NULL DEFAULT NULL COMMENT '有效期至，近效期提醒使用',
  `stock_num` int NOT NULL DEFAULT 0 COMMENT '当前库存数量',
  `purchase_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '本次入库单价（成本价）',
  `cabinet` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药柜位置：中药柜/西药柜/针剂柜等',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `supplier_id` int NULL DEFAULT NULL COMMENT '关联供应商表，来源供应商',
  `status` tinyint NULL DEFAULT 1 COMMENT '1=正常, 0=已过期/禁用/清库',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`stock_id`) USING BTREE,
  INDEX `fk_stock_medicine`(`med_id` ASC) USING BTREE,
  INDEX `fk_stock_supplier`(`supplier_id` ASC) USING BTREE,
  CONSTRAINT `fk_stock_medicine` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_stock_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存表（按批号管理）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock
-- ----------------------------
INSERT INTO `stock` VALUES (1, 1, '20250101A', '2027-01-01', 100, 8.20, '西药柜', NULL, 1, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `stock` VALUES (2, 2, '20231205B', '2026-05-20', 10, 420.00, '中药柜', NULL, 2, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `stock` VALUES (3, 3, '20240815C', '2026-08-15', 5, 5.50, '西药柜', NULL, 1, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');

-- ----------------------------
-- Table structure for stock_check
-- ----------------------------
DROP TABLE IF EXISTS `stock_check`;
CREATE TABLE `stock_check`  (
  `check_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，自增，盘点明细唯一ID',
  `check_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '盘点单号（一次盘点一个编号）',
  `med_id` int NOT NULL COMMENT '关联medicine.med_id 药品ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品批号，关联库存表',
  `system_stock` int NOT NULL COMMENT '账面库存（系统数量）',
  `actual_stock` int NOT NULL COMMENT '实际库存（盘点数量）',
  `profit_loss` int NOT NULL COMMENT '盈亏数量 = 实际 - 账面',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本单价，用于计算盈亏金额',
  `profit_loss_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '盈亏金额 = 盈亏数量 * 成本价',
  `check_user` int NOT NULL COMMENT '关联user.user_id 盘点操作员',
  `check_time` datetime NOT NULL COMMENT '盘点时间',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盈亏原因说明（破损/丢失/过期等）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '其他备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`check_id`) USING BTREE,
  INDEX `fk_check_medicine`(`med_id` ASC) USING BTREE,
  INDEX `fk_check_user`(`check_user` ASC) USING BTREE,
  CONSTRAINT `fk_check_medicine` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_check_user` FOREIGN KEY (`check_user`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存盘点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_check
-- ----------------------------

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`  (
  `supplier_id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增，供应商唯一编号',
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商全称',
  `short_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简称/常用名',
  `contact` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `telephone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '固定电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司地址',
  `business_license` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照号',
  `supply_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应类型（中药/西药/器械/综合）',
  `bank_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开户行及账号',
  `status` tinyint NULL DEFAULT 1 COMMENT '1=正常合作, 0=暂停/停用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`supplier_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供应商信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier
-- ----------------------------
INSERT INTO `supplier` VALUES (1, '某某省医药集团有限公司', '省医药', '王经理', '13800138000', NULL, NULL, NULL, '综合', NULL, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `supplier` VALUES (2, '大药王中药材批发中心', '大药王', '赵老师', '13911112222', NULL, NULL, NULL, '中药', NULL, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，自增，日志唯一ID',
  `user_id` int NULL DEFAULT NULL COMMENT '关联user.user_id，操作人ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人账号，便于直接查看',
  `real_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人真实姓名',
  `oper_module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作模块：销售/采购/库存/会员/系统等',
  `oper_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作类型：新增/修改/删除/查询/登录/作废',
  `oper_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作详细内容描述',
  `oper_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP地址',
  `oper_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `fk_log_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 1, 'admin01', '张店长', '系统登录', '登录', '管理员登录后台系统', '127.0.0.1', '2026-04-13 17:56:51', NULL);
INSERT INTO `sys_log` VALUES (2, 2, 'staff01', '李小美', '销售管理', '新增', '新增销售单：202604130001', '192.168.1.15', '2026-04-13 17:56:51', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增，用户唯一编号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号（工号）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码（加密存储）',
  `real_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店员/管理员真实姓名',
  `role` enum('admin','staff') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：admin=管理员, staff=店员',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NULL DEFAULT 1 COMMENT '1=正常, 0=禁用/离职',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户/操作员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin01', 'e10adc3949ba59abbe56e057f20f883e', '张店长', 'admin', NULL, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');
INSERT INTO `user` VALUES (2, 'staff01', 'e10adc3949ba59abbe56e057f20f883e', '李小美', 'staff', NULL, 1, NULL, '2026-04-13 17:56:51', '2026-04-13 17:56:51');

SET FOREIGN_KEY_CHECKS = 1;
