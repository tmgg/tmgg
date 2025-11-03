
package io.tmgg.modules.system.dao;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class SysUserDao extends BaseDao<SysUser> {

    @Resource
    private DbTool dbTool;


    public SysUser findByAccount(String account) {
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.eq(SysUser.Fields.account, account);
        return this.findOne(q);
    }

    /**
     * 查询状态正常的ID
     *
     * @param ids
     */
    public List<SysUser> findValid(Iterable<String> ids) {

        JpaQuery<SysUser> jpaQuery = new JpaQuery<>();
        jpaQuery.eq(SysUser.Fields.enabled, true);
        jpaQuery.in("id", ids);

        return this.findAll(jpaQuery);
    }

    /**
     * 查询状态正常的ID
     */
    public List<SysUser> findValid() {
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.eq(SysUser.Fields.enabled, true);

        return this.findAll(q);
    }

    private static final Cache<String, String> NAME_CACHE = CacheUtil.newTimedCache(1000 * 60 * 5);

    public synchronized String getNameById(String userId) {
        if (userId == null) {
            return null;
        }

        if (NAME_CACHE.containsKey(userId)) {
            return NAME_CACHE.get(userId);
        }

        SysUser user = findOne(userId);
        if (user == null) {
            return null;
        }

        String name = user.getName();
        if (name == null) {
            return null;
        }

        NAME_CACHE.put(userId, name);

        return name;
    }

    @Override
    public SysUser save(SysUser entity) {
        NAME_CACHE.clear();
        return super.save(entity);
    }

    @Override
    public void delete(SysUser entity) {
        NAME_CACHE.clear();
        super.delete(entity);
    }



    public List<SysUser> findByRoleId(String roleId) {
        JpaQuery<SysUser> q = new JpaQuery<>();
        q.isMember(SysUser.Fields.roles, new SysRole(roleId));
        return this.findAll(q);
    }


}
