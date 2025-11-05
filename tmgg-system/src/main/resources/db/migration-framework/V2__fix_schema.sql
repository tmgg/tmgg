CALL drop_column('sys_dict', 'text');
CALL drop_column('sys_dict', 'builtin');
CALL drop_column('sys_menu', 'visible');
ALTER TABLE `sys_dict` CHANGE COLUMN `name` `text` varchar(80);
