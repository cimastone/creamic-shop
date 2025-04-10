-- 创建陶瓷商店数据库
CREATE DATABASE IF NOT EXISTS ceramic_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ceramic_shop;

-- 产品模块表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    description TEXT COMMENT '产品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '产品价格',
    category VARCHAR(50) NOT NULL COMMENT '产品分类',
    image VARCHAR(255) COMMENT '产品图片URL',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE' COMMENT '产品状态：ONLINE-上架，OFFLINE-下架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 产品模块索引
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_create_time ON product(create_time);

-- 用户模块表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE INDEX idx_user_username ON `t_user`(username);
CREATE INDEX idx_user_email ON `t_user`(email);
CREATE INDEX idx_user_phone ON `t_user`(phone);

CREATE TABLE IF NOT EXISTS `t_user_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区/县',
  `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

CREATE INDEX idx_address_user_id ON `t_user_address`(user_id);

-- 订单模块表
CREATE TABLE IF NOT EXISTS `orders` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态：PENDING_PAYMENT-待支付，PAID-已支付，SHIPPED-已发货，DELIVERED-已送达，COMPLETED-已完成，CANCELLED-已取消',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    delivery_time DATETIME COMMENT '送达时间',
    complete_time DATETIME COMMENT '完成时间',
    close_time DATETIME COMMENT '取消时间',
    shipping_method VARCHAR(50) NOT NULL DEFAULT 'STANDARD' COMMENT '配送方式：STANDARD-标准配送，EXPRESS-快速配送',
    payment_method VARCHAR(50) NOT NULL DEFAULT 'ONLINE' COMMENT '支付方式：ONLINE-在线支付，COD-货到付款',
    remark TEXT COMMENT '订单备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES `t_user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE UNIQUE INDEX idx_orders_order_number ON `orders`(order_number);
CREATE INDEX idx_orders_user_id ON `orders`(user_id);
CREATE INDEX idx_orders_status ON `orders`(status);
CREATE INDEX idx_orders_create_time ON `orders`(create_time);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称（冗余）',
    product_image VARCHAR(255) COMMENT '产品图片URL（冗余）',
    quantity INT NOT NULL COMMENT '购买数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES `orders`(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);

-- 支付模块表
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_no VARCHAR(50) NOT NULL COMMENT '支付流水号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_number VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(50) NOT NULL COMMENT '支付方式：ALIPAY-支付宝，WECHAT-微信支付，CREDIT_CARD-信用卡',
    transaction_id VARCHAR(100) COMMENT '第三方支付交易号',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败，CANCELLED-已取消',
    payment_time DATETIME COMMENT '支付时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES `orders`(id),
    FOREIGN KEY (user_id) REFERENCES `t_user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

CREATE UNIQUE INDEX idx_payment_payment_no ON payment(payment_no);
CREATE INDEX idx_payment_order_id ON payment(order_id);
CREATE INDEX idx_payment_order_number ON payment(order_number);
CREATE INDEX idx_payment_user_id ON payment(user_id);
CREATE INDEX idx_payment_status ON payment(status);

-- 库存模块表
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    locked_quantity INT NOT NULL DEFAULT 0 COMMENT '锁定库存数量（被订单锁定）',
    warehouse VARCHAR(50) NOT NULL DEFAULT 'MAIN' COMMENT '仓库代码',
    last_restock_time DATETIME COMMENT '最后一次进货时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

CREATE UNIQUE INDEX idx_inventory_product_warehouse ON inventory(product_id, warehouse);

CREATE TABLE IF NOT EXISTS inventory_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '产品ID',
    change_quantity INT NOT NULL COMMENT '变更数量（正数增加，负数减少）',
    type VARCHAR(20) NOT NULL COMMENT '变更类型：IN-入库，OUT-出库，ADJUST-调整',
    reason VARCHAR(50) NOT NULL COMMENT '变更原因：PURCHASE-采购，SALE-销售，RETURN-退货，LOSS-损耗，OTHER-其他',
    operator BIGINT COMMENT '操作人ID',
    reference_no VARCHAR(50) COMMENT '关联单号（订单号/采购单号等）',
    warehouse VARCHAR(50) NOT NULL DEFAULT 'MAIN' COMMENT '仓库代码',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存历史记录表';

CREATE INDEX idx_inventory_history_product_id ON inventory_history(product_id);
CREATE INDEX idx_inventory_history_type ON inventory_history(type);
CREATE INDEX idx_inventory_history_create_time ON inventory_history(create_time);

-- 物流模块表
CREATE TABLE IF NOT EXISTS shipping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipping_no VARCHAR(50) NOT NULL COMMENT '物流单号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_number VARCHAR(50) NOT NULL COMMENT '订单编号',
    carrier VARCHAR(50) NOT NULL COMMENT '物流承运商',
    tracking_number VARCHAR(100) COMMENT '物流追踪号',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '物流状态：PENDING-待发货，SHIPPED-已发货，IN_TRANSIT-运输中，DELIVERED-已送达，RETURNED-已退回',
    ship_time DATETIME COMMENT '发货时间',
    estimated_delivery_time DATETIME COMMENT '预计送达时间',
    actual_delivery_time DATETIME COMMENT '实际送达时间',
    shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '物流费用',
    receiver VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(20) NOT NULL COMMENT '省份',
    city VARCHAR(20) NOT NULL COMMENT '城市',
    district VARCHAR(20) NOT NULL COMMENT '区/县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES `orders`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流表';

CREATE UNIQUE INDEX idx_shipping_shipping_no ON shipping(shipping_no);
CREATE INDEX idx_shipping_order_id ON shipping(order_id);
CREATE INDEX idx_shipping_order_number ON shipping(order_number);
CREATE INDEX idx_shipping_status ON shipping(status);

CREATE TABLE IF NOT EXISTS shipping_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipping_id BIGINT NOT NULL COMMENT '物流ID',
    tracking_info TEXT NOT NULL COMMENT '物流追踪信息',
    tracking_location VARCHAR(255) COMMENT '物流位置',
    tracking_time DATETIME NOT NULL COMMENT '物流追踪时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (shipping_id) REFERENCES shipping(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流追踪表';

CREATE INDEX idx_shipping_tracking_shipping_id ON shipping_tracking(shipping_id);
CREATE INDEX idx_shipping_tracking_tracking_time ON shipping_tracking(tracking_time);

-- 用户相关表
-- 角色表
CREATE TABLE IF NOT EXISTS `t_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `t_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS `t_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(50) NOT NULL COMMENT '权限编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '权限描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `t_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化角色数据
INSERT INTO `t_role` (`role_name`, `role_code`, `description`) VALUES 
('超级管理员', 'ROLE_ADMIN', '系统超级管理员角色'),
('普通用户', 'ROLE_USER', '普通用户角色');

-- 初始化超级管理员用户
INSERT INTO `t_user` (`username`, `password`, `name`, `email`, `phone`, `status`) VALUES 
('admin', '$2a$10$UCGiH1bHw5fUFpkxJJF4UO1llmaXcCFzIfiKsZ0IpNK6ha6WSbGXG', '系统管理员', 'admin@ceramicshop.com', '13800138000', 1);

-- 分配管理员角色
INSERT INTO `t_user_role` (`user_id`, `role_id`) VALUES 
(1, 1);

-- 用户收货地址表
CREATE TABLE `order_shipping_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(20) NOT NULL COMMENT '省份',
  `city` varchar(20) NOT NULL COMMENT '城市',
  `district` varchar(20) NOT NULL COMMENT '区/县',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_shipping_address_order` 
  FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) 
  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

ALTER TABLE `shipping` 
DROP FOREIGN KEY `shipping_ibfk_1`,
ADD CONSTRAINT `shipping_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
CHANGE COLUMN `order_no` `order_number` VARCHAR(50) NOT NULL COMMENT '订单编号';

ALTER TABLE `shipping`
DROP COLUMN `shipping_address`,
ADD COLUMN `province` VARCHAR(20) NOT NULL COMMENT '省份',
ADD COLUMN `city` VARCHAR(20) NOT NULL COMMENT '城市',
ADD COLUMN `district` VARCHAR(20) NOT NULL COMMENT '区/县',
ADD COLUMN `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址';

ALTER TABLE `order_shipping_address`
DROP COLUMN `user_id`,
DROP COLUMN `is_default`,
DROP INDEX `idx_user_id`;

ALTER TABLE `order_item`
DROP FOREIGN KEY `order_item_ibfk_1`,
ADD CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `shipping`
DROP FOREIGN KEY `shipping_ibfk_1`,
ADD CONSTRAINT `shipping_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

ALTER TABLE `orders`
DROP COLUMN `shipping_address_id`,
DROP COLUMN `receiver_name`,
DROP COLUMN `receiver_phone`, 
DROP COLUMN `receiver_province`,
DROP COLUMN `receiver_city`,
DROP COLUMN `receiver_district`,
DROP COLUMN `receiver_detail_address`; 