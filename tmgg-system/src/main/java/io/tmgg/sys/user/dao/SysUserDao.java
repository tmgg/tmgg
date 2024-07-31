
package io.tmgg.sys.user.dao;

import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.user.entity.SysUser;
import cn.moon.dbtool.DbTool;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
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
        jpaQuery.eq(SysUser.Fields.status, CommonStatus.ENABLE);
        jpaQuery.in("id", ids);

        return this.findAll(jpaQuery);
    }

    /**
     * 查询状态正常的ID
     *
     */
    public List<SysUser> findValid(){
        JpaQuery<SysUser> jpaQuery = new JpaQuery<>();
        jpaQuery.eq(SysUser.Fields.status, CommonStatus.ENABLE);

        return this.findAll(jpaQuery);
    }

    private final Map<String,String> cache = new HashMap();

    public synchronized String getNameById(String userId) {
        if(userId == null ) {
            return  null;
        }

        if(cache.isEmpty()) {
            Map<String, String> list = this.findUserNameMap();
            cache.putAll(list);
        }

        return cache.get(userId);
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

    public Map<String,String> findUserNameMap(){
        String sql = "select id, name from sys_user";

        return dbTool.findDict(sql);
    }

    public List<SysUser> findByRoleId(String roleId) {
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.isMember(SysUser.Fields.roles, new SysRole(roleId));

        return this.findAll(q);
    }
}
