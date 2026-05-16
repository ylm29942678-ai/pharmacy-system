-- 将库存预警下限从药品表迁移到库存表。
-- 已有数据库执行一次即可；新建数据库可直接使用 docs/pms_db.sql。

ALTER TABLE stock
    ADD COLUMN stock_min int NULL DEFAULT 0 COMMENT '库存预警下限' AFTER stock_num;

UPDATE stock s
JOIN medicine m ON s.med_id = m.med_id
SET s.stock_min = COALESCE(m.stock_min, 0);

ALTER TABLE medicine
    DROP COLUMN stock_min;
