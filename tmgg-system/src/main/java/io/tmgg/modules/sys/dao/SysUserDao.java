
package io.tmgg.modules.sys.dao;

import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class SysUserDao extends BaseDao<SysUser> {

    @Resource
    private DbTool dbTool;

    public SysUser findByAccount(String account){
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.eq(SysUser.Fields.account, account);
        return this.findOne(q);
    }

    /**
     * 查询状态正常的ID
     * @param ids
     *
     */
    public List<SysUser> findValid(Iterable<String> ids){

        JpaQuery<SysUser> jpaQuery = new JpaQuery<>();
        jpaQuery.eq(SysUser.Fields.enabled, true);
        jpaQuery.in("id", ids);

        return this.findAll(jpaQuery);
    }

    /**
     * 查询状态正常的ID
     *
     */
    public List<SysUser> findValid(){
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.eq(SysUser.Fields.enabled, true);

        return this.findAll(q);
    }

    private final Map<String,Object> cache = new HashMap<>();

    public synchronized String getNameById(String userId) {
        if(userId == null ) {
            return  null;
        }

        if(cache.isEmpty()) {
            Map<String, Object> list = this.findUserNameMap();
            cache.putAll(list);
        }

        return (String) cache.get(userId);
    }

    @Override
    public SysUser save(SysUser entity) {
        cache.clear();
        return super.save(entity);
    }

    @Override
    public void delete(SysUser entity) {
        cache.clear();
        super.delete(entity);
    }

    public Map<String,Object> findUserNameMap(){
        String sql = "select id, code from sys_user";

        return dbTool.findDict(sql);
    }

    public List<SysUser> findByRoleId(String roleId) {
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.isMember(SysUser.Fields.roles, new SysRole(roleId));

        return this.findAll(q);
    }
}
