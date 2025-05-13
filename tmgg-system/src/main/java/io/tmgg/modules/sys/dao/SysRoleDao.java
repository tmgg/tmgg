
package io.tmgg.modules.sys.dao;


import io.tmgg.persistence.BaseDao;
import io.tmgg.persistence.specification.JpaQuery;
import io.tmgg.modules.sys.entity.SysRole;
import org.springframework.stereotype.Repository;

/**
 * 系统角色
 *

 *
 */
@Repository
public class SysRoleDao extends BaseDao<SysRole> {

    public SysRole findByCode(String code) {

        JpaQuery<SysRole> q = new JpaQuery<>();
        q.eq(SysRole.Fields.code, code);
        return this.findOne(q);
    }

    public long countByCode(String code) {

        JpaQuery<SysRole> q = new JpaQuery<>();
        q.eq(SysRole.Fields.code, code);
        return this.count(q);
    }
}
