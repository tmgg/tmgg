package io.tmgg.flowable.mgmt.dao;

import io.tmgg.flowable.mgmt.entity.SysFlowableModel;
import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class SysFlowableModelDao extends BaseDao<SysFlowableModel> {

    public SysFlowableModel findByCode(String code) {
        JpaQuery<SysFlowableModel> q = new JpaQuery<>();
        q.eq(SysFlowableModel.Fields.code, code);
        return this.findOne(q);
    }




}
