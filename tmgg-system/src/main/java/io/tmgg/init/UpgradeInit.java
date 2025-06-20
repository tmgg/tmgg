package io.tmgg.init;

import io.tmgg.dbtool.DbTool;
import io.tmgg.modules.system.dao.SysDictDao;
import io.tmgg.modules.system.dao.SysDictItemDao;
import io.tmgg.modules.system.entity.OrgType;
import io.tmgg.modules.system.entity.SysDict;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Set;

@Slf4j
@Component
public class UpgradeInit {

    @Resource
    private DbTool db;

    @Resource
    private SysDictDao sysDictDao;

    @Resource
    private SysDictItemDao sysDictItemDao;

    public void init() throws SQLException {
        log.info("开始升级程序，主要针对tmgg前框架的升级");

        {
            SysDict old = sysDictDao.findByCode("ORG_TYPE");
            if(old != null){
                sysDictItemDao.deleteByPid(old.getId());
                sysDictDao.delete(old);
            }
        }
        {
            SysDict old = sysDictDao.findByCode("SEX");
            if(old != null){
                sysDictItemDao.deleteByPid(old.getId());
                sysDictDao.delete(old);
            }
        }


        {  // sys_role_menu 已经不再使用
            Set<String> tableNames = db.getTableNames();
            if(tableNames.contains("sys_role_menu")){
                db.executeQuietly("drop table sys_role_menu");
            }
        }


        {
            Set<String> columns = db.getTableColumns("sys_dict_item");
            if(columns.contains("key_")){
                String sql = "ALTER TABLE sys_dict_item DROP COLUMN key_" ;
                db.executeQuietly(sql);
            }
        }

        {
            // org 的类型调整为int
            String type = db.getTableColumnType("sys_org", "type");
            boolean isInt = StringUtils.containsIgnoreCase(type, "INT"); //  mysql 返回的int
            if(!isInt){
                log.info("修改sys_org的type为数字");

                db.execute("update sys_org set type = " + OrgType.UNIT + " where type=?", "UNIT");
                db.execute("update sys_org set type = " + OrgType.DEPT + " where type=?", "DEPT");


                String sql = "ALTER TABLE sys_org MODIFY COLUMN type int";
                db.executeQuietly(sql);
            }
        }

        // 删除日志文件
        {
            db.executeQuietly("drop table sys_job_logging");
        }

        // 权限字段扩容
        db.executeQuietly("ALTER TABLE `sys_menu` MODIFY COLUMN `perm` varchar(100) ");
    }
}
