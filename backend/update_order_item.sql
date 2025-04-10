-- Rename table
ALTER TABLE order_item RENAME TO order_items;
-- Add missing column
ALTER TABLE order_items ADD COLUMN product_specs VARCHAR(100) DEFAULT NULL COMMENT '商品规格' AFTER product_image;
-- Rename columns to match PO
ALTER TABLE order_items CHANGE COLUMN unit_price price DECIMAL(10,2) NOT NULL COMMENT '单价',
CHANGE COLUMN total_price subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额';
-- Update indexes
DROP INDEX idx_order_item_order_id ON order_items;
DROP INDEX idx_order_item_product_id ON order_items;
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
