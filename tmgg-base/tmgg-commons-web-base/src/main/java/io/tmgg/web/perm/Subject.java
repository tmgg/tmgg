package io.tmgg.web.perm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// 仅部分字段有getter， setter
@Getter
@Setter
@Slf4j
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;


    private String id;
    private String account;

    private String name;
    private String unitId; // 所属机构， 如公司，非内部小组或部门
    private String deptId;


    // ------------- 权限相关----------

    private Set<String> permissions = new HashSet<>();
    private Set<String> roles = new HashSet<>();





    private Set<String> orgPermissions = new HashSet<>(); // 数据权限

    public Collection<String> getOrgPermissions() {
        return orgPermissions;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public boolean hasOrgPermission(String orgId) {
        if (orgId == null || orgId.isEmpty()) {
            return false;
        }


        return orgPermissions.contains(orgId);
    }




    public boolean hasPermission(String input) {
        if (StringUtils.isEmpty(input) ) {
            return true;
        }

        if(CollectionUtils.isEmpty(permissions)){
            return false;
        }

        if (permissions.contains("*")) {
            return true;
        }

        // 处理url的情况
        if (input.contains("/")) {
            input = StringUtils.removeStart(input, "/");
            input = StringUtils.removeEnd(input, "/");
            input = StringUtils.replace(input, "/", ":");
        }

        // 处理用户有user:list时， 同时也有user(非user:*)的权限（方便获取菜单）
        if(!input.contains(":")){
            for (String permission : permissions) {
                String[] arr = permission.split(":");
                if(arr[0].equals(permission)){
                    return true;
                }
            }
        }


        return permissions.contains(input);
    }


    public void addRole(String role) {
        this.roles.add(role);
    }


    public boolean hasRole(String role) {
        return roles.contains(role);
    }


    @Override
    public String toString() {
        return "用户 " + account;
    }






}
