package io.tmgg.flowable.dao;

import io.tmgg.flowable.entity.ConditionVariable;
import io.tmgg.flowable.entity.ConditionVariableListHandler;
import io.tmgg.flowable.entity.FlowModel;
import cn.tmgg.dbtool.Converters;
import cn.tmgg.dbtool.DbTool;
import cn.moon.lang.BeanTool;
import cn.moon.lang.MapTool;
import cn.moon.lang.StrTool;
import cn.moon.lang.json.JsonTool;
import cn.moon.lang.web.Page;
import cn.moon.lang.web.Pageable;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ModelDao {

    public static final String TABLE_NAME = "flow_model";

    @Resource
    DbTool jdbc;

    @PostConstruct
    public void init() {
        log.debug("判断流程表是否存在");
        String sql = "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (" +
                     "  `id` varchar(50) NOT NULL," +
                     "  `create_time` datetime(6) DEFAULT NULL," +
                     "  `create_user` varchar(50) DEFAULT NULL," +
                     "  `update_time` datetime(6) DEFAULT NULL," +
                     "  `update_user` varchar(50) DEFAULT NULL," +
                     "  `code` varchar(255) NOT NULL," +
                     "  `content` longtext," +
                     "  `description` varchar(255) DEFAULT NULL," +
                     "  `name` varchar(255) DEFAULT NULL," +
                     "  `on_process_completed` varchar(255) DEFAULT NULL," +
                     "  `form_link` varchar(255) DEFAULT NULL," +
                     "  `condition_variable_list` longtext," +
                     "  PRIMARY KEY (`id`)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        log.debug(sql);

        jdbc.execute(sql);


        Converters.getInstance().register(new ConditionVariableListHandler());

    }

    public FlowModel findByCode(String code) {
        String sql = "select * from " + TABLE_NAME + " where code=?";
        return jdbc.findOne(FlowModel.class, sql, code);
    }

    public FlowModel save(FlowModel model) throws JsonProcessingException {
        List<ConditionVariable> conditionVariableList = model.getConditionVariableList();
        String conditionVariableListJson = JsonTool.toJson(conditionVariableList);

        Map<String, Object> data = BeanTool.copyToMap(FlowModel.class, model);

        // 转换json
        data.put(FlowModel.Fields.conditionVariableList, conditionVariableListJson);

        MapTool.underlineKeys(data);


        if (StringUtils.isEmpty(model.getId())) {
            data.put("id", StrTool.uuid());
            jdbc.insert(TABLE_NAME, data);
        } else {
            FlowModel oldModel = findOne(model.getId());
            if (oldModel == null) {
                jdbc.insert(TABLE_NAME, data);
            } else {
                jdbc.updateById(TABLE_NAME, data);
            }
        }


        return model;
    }

    public FlowModel findOne(String id) {
        String sql = "select * from " + TABLE_NAME + " where id=?";
        return jdbc.findOne(FlowModel.class, sql, id);
    }

    public void deleteById(String id) {
        String sql = "delete  from " + TABLE_NAME + " where id=?";
        jdbc.execute(sql, id);
    }

    public Page<FlowModel> findAll(String keyword, Pageable pageable) {
        String sql = "select * from " + TABLE_NAME + " where 1=1 ";
        List<String> params = new ArrayList<>();
        if (StringUtils.isNotEmpty(keyword)) {
            sql += " and (name like ? or code like ?)";
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        Page<FlowModel> list = jdbc.findAll(FlowModel.class, pageable, sql, params.toArray());

        return list;
    }


}
