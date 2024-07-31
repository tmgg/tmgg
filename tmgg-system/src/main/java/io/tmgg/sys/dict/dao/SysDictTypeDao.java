
package io.tmgg.sys.dict.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.sys.dict.entity.SysDictType;
import org.springframework.stereotype.Repository;

/**
 * 系统字典类型
 */
@Repository
public class SysDictTypeDao extends BaseDao<SysDictType> {

    public SysDictType findByCode(String code) {
        return this.findOneByField(SysDictType.Fields.code, code);
    }

}
