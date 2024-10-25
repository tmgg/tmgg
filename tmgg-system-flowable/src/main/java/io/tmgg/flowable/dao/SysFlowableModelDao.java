package io.tmgg.flowable.dao;

import io.tmgg.flowable.entity.SysFlowableModel;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.dao.BaseDao;
import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class SysFlowableModelDao extends BaseDao<SysFlowableModel> {

    public SysFlowableModel findByCode(String code) {
        return this.findOneByField(SysFlowableModel.Fields.code, code);
    }




}
