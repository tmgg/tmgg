
package io.tmgg.sys.user.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.AutoFill;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.core.jpa.fill.AutoFillOrgLabelStrategy;
import io.tmgg.sys.org.entity.SysOrg;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.user.enums.DataPermType;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.annotation.Lazy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "sys_user")
@FieldNameConstants
@Remark("系统用户")
public class SysUser extends BaseEntity {

    public static SysUser of(String id) {
        SysUser t = new SysUser();
        t.setId(id);
        return t;
    }

    public SysUser() {
    }

    public SysUser(String id) {
        this.id = id;
    }


    /**
     * 所属机构 (公司，单位级别）
     */
    @Remark("所属机构")
    private String orgId;


    @Transient
    @AutoFill(value = AutoFillOrgLabelStrategy.class, sourceField = "orgId")
    private String orgLabel;


    @Remark("所属部门")
    private String deptId;

    @Transient
    @AutoFill(value = AutoFillOrgLabelStrategy.class, sourceField = "deptId")
    private String deptLabel;


    @Remark("账号")
    @NotNull(message = "账号不能为空")
    @Column(unique = true)
    private String account;

    /**
     * 密码， 转换json时不显示，但可接受前端设置
     */
    @Remark("密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Remark("姓名")
    @Column(length = 50)
    private String name;



    @Remark("电话")
    @Column(length = 11)
    private String phone;


    @Remark("邮箱")
    @Column(length = 30)
    private String email;

    /**
     * 最后登陆IP
     */
    @Column(length = 15)
    private String lastLoginIp;

    /**
     * 最后登陆时间
     */
    private Date lastLoginTime;



    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @Remark("状态")
    @NotNull
    @Column(length = 10)
    private CommonStatus status;



    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
    Set<SysRole> roles = new HashSet<>();


    @Transient
    List<String> roleNames;

    @Transient
    List<String> roleIds;

    // 数据权限类型
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    DataPermType dataPermType;

    @JsonIgnore
    @Lazy
    @ManyToMany
    @JoinTable(name = "sys_user_org_data_scope", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "org_id"))
    List<SysOrg> orgDataScope = new ArrayList<>();



    @Override
    public void beforeSaveOrUpdate() {
        super.beforeSaveOrUpdate();
        if(dataPermType == null ){
            dataPermType = DataPermType.ORG_AND_CHILDREN;
        }
    }

    @Override
    public String toString() {
        return name + " " + phone;
    }
}
