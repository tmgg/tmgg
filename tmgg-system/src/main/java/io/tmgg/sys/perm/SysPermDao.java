
package io.tmgg.sys.perm;


import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.web.enums.MenuType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class SysPermDao extends BaseDao<SysPerm> {



    public List<SysPerm> findMenuVisible() {
        JpaQuery<SysPerm> query = new JpaQuery<>();
        query.ne(SysPerm.Fields.type, MenuType.BTN);
        query.eq(SysPerm.Fields.visible, true);

        List<SysPerm> list = this.findAll(query, Sort.by(SysPerm.Fields.seq));
        return list;
    }

    public List<SysPerm> findAllValid() {
        JpaQuery<SysPerm> query = new JpaQuery<>();

        List<SysPerm> list = this.findAll(query, Sort.by(SysPerm.Fields.seq));
        return list;
    }


    public SysPerm findByPerm(String perm) {
        JpaQuery<SysPerm> query = new JpaQuery<>();
        query.eq(SysPerm.Fields.perm, perm);
        return this.findOne(query);
    }

    public List<SysPerm> findByPerms(List<String> perms) {
        JpaQuery<SysPerm> query = new JpaQuery<>();
        query.in(SysPerm.Fields.perm, perms);
        return this.findAll(query);
    }


}
