
package io.tmgg.sys.menu.dao;


import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.menu.entity.SysMenu;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.enums.MenuType;
import io.tmgg.web.enums.YesOrNotEnum;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统菜单
 */
@Repository
public class SysMenuDao extends BaseDao<SysMenu> {



    public List<SysMenu> findChildren(String id) {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.ne(SysMenu.Fields.pid, id);
        List<SysMenu> list = this.findAll(query, Sort.by(SysMenu.Fields.seq));
        return list;
    }


    /**
     * 获得菜单
     *
     *
     */
    public List<SysMenu> findMenuVisible() {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.ne(SysMenu.Fields.type, MenuType.BTN);
        query.eq(SysMenu.Fields.status, CommonStatus.ENABLE);
        query.ne(SysMenu.Fields.visible, true);

        List<SysMenu> list = this.findAll(query, Sort.by(SysMenu.Fields.seq));
        return list;
    }

    public List<SysMenu> findAllValid() {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.eq(SysMenu.Fields.status, CommonStatus.ENABLE);

        List<SysMenu> list = this.findAll(query, Sort.by(SysMenu.Fields.seq));
        return list;
    }

    /**
     * 查询公共菜单，让普通用户也能看到的菜单， 简化角色配置
     * 如果菜单没有子节点
     *
     *
     */
    public List<SysMenu> findAllValidAndPublic() {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.eq(SysMenu.Fields.status, CommonStatus.ENABLE);
        query.eq(SysMenu.Fields.code, null);
        query.eq(SysMenu.Fields.visible, true);
        query.eq(SysMenu.Fields.type, MenuType.MENU);

        List<SysMenu> list = this.findAll(query, Sort.by(SysMenu.Fields.seq));

        return list;
    }


    public SysMenu findByPerm(String btnPerm) {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.eq(SysMenu.Fields.permission, btnPerm);
        return this.findOne(query);
    }

    public SysMenu findByCode(String code) {
        JpaQuery<SysMenu> query = new JpaQuery<>();
        query.eq(SysMenu.Fields.code, code);
        return this.findOne(query);
    }


}
