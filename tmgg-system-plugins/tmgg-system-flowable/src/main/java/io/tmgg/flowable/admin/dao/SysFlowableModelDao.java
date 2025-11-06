package io.tmgg.flowable.admin.dao;

import io.tmgg.flowable.admin.entity.ConditionVariable;
import io.tmgg.flowable.admin.entity.FormItem;
import io.tmgg.flowable.admin.entity.SysFlowableModel;
import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class SysFlowableModelDao extends BaseDao<SysFlowableModel> {

    public SysFlowableModel findByCode(String code) {
        JpaQuery<SysFlowableModel> q = new JpaQuery<>();
        q.eq(SysFlowableModel.Fields.code, code);
        return this.findOne(q);
    }


    @Transactional
    public void init(String key, String name, List<ConditionVariable> vars, List<FormItem> formKeyList) {
        log.info("初始化流程定义 {} {} {} ", key, name,  vars);
        SysFlowableModel model = this.findByCode(key);
        if (model == null) {
            model = new SysFlowableModel();
        }

        if(vars == null){
            vars = new ArrayList<>();
        }

        model.setCode(key);
        model.setName(name);
        model.setConditionVariableList(vars);
        model.setFormKeyList(formKeyList);
        this.save(model);
    }

}
