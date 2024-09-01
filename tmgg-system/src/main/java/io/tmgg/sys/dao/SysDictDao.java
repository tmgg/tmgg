
package io.tmgg.sys.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.sys.entity.SysDict;
import org.springframework.stereotype.Repository;

@Repository
public class SysDictDao extends BaseDao<SysDict> {

    public SysDict findByCode(String code) {
        return this.findOneByField(SysDict.Fields.code, code);
    }

}
