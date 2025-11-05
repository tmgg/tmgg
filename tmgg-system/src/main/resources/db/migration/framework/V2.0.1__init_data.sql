INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('email.from', '邮件发送账号', '410518072@qq.com', NULL, 110, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('email.pass', '邮件发送密码', NULL, NULL, 111, 'password');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.baseUrl', '请求基础地址', NULL, '非必填，可用于拼接完整请求地址', NULL, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.default.password', '默认密码', null, NULL, NULL, 'password');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.jwtSecret', 'jwt密码', NULL, NULL, 1000, 'password');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.login.lock.maxAttempts', '登录错误超过多少次锁定', '3', NULL, NULL, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.multiDeviceLogin', '多端登录', 'true', '是否允许账号在多个地方登录', NULL, 'boolean');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.printAssertException', '是否打印Assert方法抛出的异常', 'true', NULL, NULL, 'boolean');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.rsa.privateKey', 'RSA私钥', NULL, NULL, NULL, 'password');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.rsa.publicKey', 'RSA公钥', NULL, NULL, NULL, 'password');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.captcha', '开启验证码', 'false', '登录时显示验证码', NULL, 'boolean');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.captchaType', '验证码类型', NULL, 'random,math', NULL, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.copyright', '版权信息', NULL, '显示在页脚', NULL, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.loginBackground', '登录背景图', NULL, NULL, NULL, 'image');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.loginBoxBottomTip', '登录框下面的提示', '当前非涉密网络，严禁传输处理涉密信息', NULL, NULL, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.logo', '站点logo', NULL, NULL, NULL, 'image');
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.title', '站点标题', '管理系统', NULL, NULL, NULL);
INSERT INTO sys_config (id, label, default_value, remark, seq, value_type) VALUES ('sys.siteInfo.waterMark', '开启水印', NULL, '在所有页面增加水印', NULL, 'boolean');



INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('biz', NULL, NULL, '业务模块', NULL, 0, 'DIR');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sys', NULL, NULL, '系统管理', NULL, 1000, 'DIR');

INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysOrg', 'sys', 'ApartmentOutlined', '机构管理', '/system/org', 1010, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysUser', 'sys', 'UserOutlined', '用户管理', '/system/user', 1011, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysRole', 'sys', 'IdcardOutlined', '角色管理', '/system/role', 1012, 'MENU');

INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('jobMgr', 'sys', 'ScheduleOutlined', '作业调度', NULL, 7, 'DIR');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('job', 'jobMgr', 'OrderedListOutlined', '作业管理', '/job', 1012, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('jobStatus', 'jobMgr', 'FundViewOutlined', '作业监控', '/job/status', 3, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('jobLog', 'jobMgr', 'FileOutlined', '作业日志', '/job/logList', 2, 'MENU');


INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('machine', 'sysMonitor', NULL, '服务监控', '/system/machine', 1, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('dev', 'sys', 'JavaOutlined', '开发管理', '/code', 200, 'MENU');

INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('api', 'sys', 'ApiOutlined', '开放接口', NULL, 201, 'DIR');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('apiAccount', 'api', 'ApiOutlined', '账户管理', '/api', 1, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('apiAccountResource', 'api', 'ApiOutlined', '账户授权', NULL, 2, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('apiResource', 'api', 'ApiOutlined', '接口管理', '/api/resource', 3, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('apiAccessLog', 'api', 'ApiOutlined', '访问记录', '/api/accessLog', 4, 'MENU');



INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysManual', 'sys', 'CopyOutlined', '操作手册', '/system/sysManual', 4, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysOnlineUser', 'sysMonitor', NULL, '在线用户', '/system/onlineUser', 4, 'MENU');

INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysConfig', 'sys', 'SettingOutlined', '系统参数', '/system/config', 5, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysDict', 'sys', 'FileSearchOutlined', '数据字典', '/system/dict', 5, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysLog', 'sysMonitor', NULL, '操作日志', '/system/log', 5, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysFile', 'sys', 'FolderOpenOutlined', '存储文件', '/system/file', 6, 'MENU');

INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sys_entity_gen', 'dev', 'JavaScriptOutlined', '生成代码', '/dev/code', 10, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysMenu', 'sys', 'AppstoreOutlined', '菜单管理', '/system/menu', 10, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysMenuBadge', 'sys', 'MessageOutlined', '菜单红点', '/system/menuBadge', 11, 'MENU');
INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('requestTest', 'dev', 'LinkOutlined', '请求测试', '/dev/requestTest', 40, 'MENU');

INSERT INTO sys_menu (id, pid, icon, name, path, seq, type) VALUES ('sysMonitor', 'sys', 'DesktopOutlined', '系统监控', NULL, 199, 'DIR');

