package io.tmgg.flowable.dao;

import io.tmgg.dbtool.Converters;
import io.tmgg.dbtool.DbTool;
import io.tmgg.flowable.entity.ConditionVariable;
import io.tmgg.flowable.entity.ConditionVariableListHandler;
import io.tmgg.flowable.entity.SysFlowableModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.lang.BeanTool;
import io.tmgg.lang.JsonTool;
import io.tmgg.lang.MapTool;
import io.tmgg.lang.StrTool;
import io.tmgg.lang.dao.BaseDao;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SysFlowableModelDao extends BaseDao<SysFlowableModel> {

    public SysFlowableModel findByCode(String code) {
        return this.findOneByField(SysFlowableModel.Fields.code, code);
    }




}
