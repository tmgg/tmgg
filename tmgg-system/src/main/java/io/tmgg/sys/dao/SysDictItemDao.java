
package io.tmgg.sys.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.entity.SysDictItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统字典值
 *

 *
 */
@Repository
public class SysDictItemDao extends BaseDao<SysDictItem> {

    @Transactional
    public void deleteByTypeId(String typeId) {
        JpaQuery<SysDictItem> query = new JpaQuery<>();

        query.eq(SysDictItem.Fields.typeId, typeId);

        List<SysDictItem> all = this.findAll(query);


        this.deleteAll(all);

    }

    public void deleteByTypeIdPhysical(String typeId) {
        deleteAll(new JpaQuery<>().eq(SysDictItem.Fields.typeId, typeId));
    }

}
