DELIMITER $$

DROP PROCEDURE IF EXISTS drop_column
$$

CREATE PROCEDURE drop_column(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64)
)
BEGIN
    DECLARE column_exists INT;

    -- 检查列是否存在
    SELECT COUNT(*)
    INTO column_exists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;

    -- 如果存在，则删除该列
    IF column_exists > 0 THEN
        SET @sql = CONCAT('ALTER TABLE ',  p_table_name, ' DROP COLUMN ', p_column_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('Column "', p_column_name, '" dropped from table "', p_table_name, '".') AS message;
    ELSE
        SELECT CONCAT('Column "', p_column_name, '" does NOT exist in table "', p_table_name, '".') AS message;
    END IF;
END$$

DELIMITER ;
