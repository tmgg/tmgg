package io.tmgg.web.perm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// 仅部分字段有getter， setter
@Getter
@Setter
@Slf4j
public class Subject {

    Subject() {
        // 友元构造
        this.authenticated = false;
    }

    public String getAccount() {
        // 默认对象创建，但未登录
        log.debug("authenticated={}, {}", this.isAuthenticated(), this.authenticated);
        Assert.state(this.authenticated, "错误，请先调用 isAuthenticated 方法判断， 返回true之后再调用getAccount方法");

        return account;
    }

    private String id;


    private String unitId; // 所属机构， 如公司，非内部小组或部门
    private String unitName;

    private String deptId;
    private String deptName;


    private String account;

    private String password;


    private String nickName;


    private String name;


    private String avatar;


    private String phone;




    // ------------- 权限相关----------

    private Set<String> permissions = new HashSet<>();
    private Set<String> roles = new HashSet<>();

    private boolean authenticated; //  是否登录
    private String token;
    private Session session;


    private Set<String> orgPermissions = new HashSet<>(); // 数据权限

    public Collection<String> getOrgPermissions() {
        if (sealed) {
            return Collections.unmodifiableCollection(orgPermissions);
        }
        return orgPermissions;
    }

    public Collection<String> getPermissions() {
        if (sealed) {
            return Collections.unmodifiableCollection(permissions);
        }
        return permissions;
    }

    public boolean hasOrgPermission(String orgId) {
        if (orgId == null || orgId.isEmpty()) {
            return false;
        }


        return orgPermissions.contains(orgId);
    }


    public boolean isPermitted(String perm) {
        if (StringUtils.isEmpty(perm) || CollectionUtils.isEmpty(permissions)) {
            return false;
        }
        if (permissions.contains("*")) {
            return true;
        }

        // 处理url的情况
        if (perm.contains("/")) {
            perm = StringUtils.removeStart(perm, "/");
            perm = StringUtils.removeEnd(perm, "/");
            perm = StringUtils.replace(perm, "/", ":");
        }


        return permissions.contains(perm);
    }

    public boolean hasPermission(String code) {
        return isPermitted(code);
    }


    public void addRole(String role) {
        Assert.state(!sealed, "状态已封存，不可改变");
        this.roles.add(role);
    }


    public Object getPrincipal() {
        return id;
    }


    public boolean hasRole(String role) {
        return roles.contains(role);
    }


    // 友元函数
    void login(String token) {
        this.token = token;
        this.authenticated = true;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }


    public Session getSession() {
        if (this.session == null) {
            this.session = new Session();
        }
        return this.session;
    }


    public void logout() {
        this.token = null;
        this.authenticated = false;
    }


    @Override
    public String toString() {
        return "用户 " + account;
    }



    private boolean sealed = false;

    /**
     * 封存权限等信息，不可轻易改变状态
     */
    public void sealed() {
        this.sealed = true;
    }
}
