-- 1. 如果存储过程已存在，先删除
DROP PROCEDURE IF EXISTS drop_column_if_exists;

-- 2. 重新创建存储过程
DELIMITER //

CREATE PROCEDURE drop_column_if_exists(
    IN p_table_name VARCHAR(100),
    IN p_column_name VARCHAR(100)
)
BEGIN
    DECLARE column_exists INT;

    -- 检查列是否存在
    SELECT COUNT(*)
    INTO column_exists
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = p_table_name
      AND column_name = p_column_name;

    -- 如果存在，则删除列
    IF column_exists > 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP COLUMN ', p_column_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SELECT CONCAT('✅ 列 "', p_column_name, '" 已从表 "', p_table_name, '" 中删除') AS message;
    ELSE
        SELECT CONCAT('ℹ️ 列 "', p_column_name, '" 在表 "', p_table_name, '" 中不存在') AS message;
    END IF;
END //

DELIMITER ;
