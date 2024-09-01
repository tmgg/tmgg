
package io.tmgg.sys.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.sys.entity.SysDictType;
import org.springframework.stereotype.Repository;

@Repository
public class SysDictDao extends BaseDao<SysDictType> {

    public SysDictType findByCode(String code) {
        return this.findOneByField(SysDictType.Fields.code, code);
    }

}
