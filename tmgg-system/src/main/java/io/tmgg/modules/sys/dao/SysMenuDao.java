
package io.tmgg.modules.sys.dao;


import cn.hutool.core.lang.Validator;
import io.tmgg.persistence.BaseDao;
import io.tmgg.persistence.specification.JpaQuery;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.web.enums.MenuType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class SysMenuDao extends BaseDao<SysMenu> {



    public List<SysMenu> findMenuVisible() {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.ne(SysMenu.Fields.type, MenuType.BTN);
        query.eq(SysMenu.Fields.visible, true);

        List<SysMenu> list = this.findAll(query, Sort.by(SysMenu.Fields.seq));
        return list;
    }

    public List<SysMenu> findAllValid() {
        JpaQuery<SysMenu> query = new JpaQuery<>();

        List<SysMenu> list = this.findAll(query, Sort.by(SysMenu.Fields.seq));
        return list;
    }


    public String findChineseNameByPerm(String perm) {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.eq(SysMenu.Fields.perm, perm);
        List<SysMenu> list = this.findAll(query);
        for (SysMenu sysMenu : list) {
            String name = sysMenu.getName();
            if(Validator.hasChinese(name)){
                return name;
            }
        }
        return null;
    }

    public List<SysMenu> findByPerms(List<String> perms) {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.in(SysMenu.Fields.perm, perms);
        return this.findAll(query);
    }


}
