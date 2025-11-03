ALTER TABLE `sys_dict` DROP COLUMN text;
ALTER TABLE `sys_dict` CHANGE COLUMN `name` `text` varchar(80);
ALTER TABLE `sys_dict` DROP COLUMN builtin;
