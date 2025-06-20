
package io.tmgg.modules.system.dao;


import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.modules.system.entity.SysRole;
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
