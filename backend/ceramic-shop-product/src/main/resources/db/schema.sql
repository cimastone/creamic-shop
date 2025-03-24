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

-- 创建索引
CREATE INDEX idx_category ON product(category);
CREATE INDEX idx_status ON product(status);
CREATE INDEX idx_create_time ON product(create_time); 