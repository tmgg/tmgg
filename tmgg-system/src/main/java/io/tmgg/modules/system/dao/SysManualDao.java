package io.tmgg.modules.system.dao;

import io.tmgg.modules.system.entity.SysManual;
import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class SysManualDao extends BaseDao<SysManual> {


    public int findMaxVersion(String name){
        JpaQuery<SysManual> q = new JpaQuery<>();
        q.eq(SysManual.Fields.name,name);

        SysManual e = this.findTop1(q, Sort.by(Sort.Direction.DESC, SysManual.Fields.version));

        return e == null ? 0 : e.getVersion();
    }

}

