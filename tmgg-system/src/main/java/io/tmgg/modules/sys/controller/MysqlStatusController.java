package io.tmgg.modules.sys.controller;

import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("mysqlStatus")
public class MysqlStatusController {

    @Resource
    DbTool db;

    @RequestMapping("dbSize")
    public AjaxResult dbSize() {
        String sql = """
                select
                table_schema as '数据库',
                sum(table_rows) as '记录数',
                sum(truncate(data_length/1024/1024, 1)) as '数据容量(MB)',
                sum(truncate(index_length/1024/1024, 1)) as '索引容量(MB)'
                from information_schema.tables
                group by table_schema
                order by sum(data_length) desc, sum(index_length) desc
                """;
        List<Map<String, Object>> list = db.findAll(sql);

        return AjaxResult.ok().data(list);
    }

    @RequestMapping("tableSize")
    public AjaxResult tableSize() {
        String sql = """
                select
                table_schema as '数据库',
                table_name as '表名',
                table_rows as '记录数',
                truncate(data_length/1024/1024, 1) as '数据容量(MB)',
                truncate(index_length/1024/1024, 1) as '索引容量(MB)'
                from information_schema.tables
                order by table_schema,table_name;
                """;
        List<Map<String, Object>> list = db.findAll(sql);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).put("id", i);
        }

        return AjaxResult.ok().data(list);
    }

}