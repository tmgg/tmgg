
package io.tmgg.modules.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.framework.data.append.AutoAppendOrgLabelStrategy;
import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.AutoAppendField;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.annotation.Lazy;

import java.util.*;

@Getter
@Setter
@Entity
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
        this.setId(id);

    }


    /**
     * 所属机构 (公司，单位级别）
     */
    @Remark("所属机构")
    @AutoAppendField(value = AutoAppendOrgLabelStrategy.class)
    private String unitId;

    @Excel(name = "单位")
    @Transient
    private String unitLabel;

    @AutoAppendField(value = AutoAppendOrgLabelStrategy.class)
    @Remark("所属部门")
    private String deptId;

    @Excel(name = "部门")
    @Transient
    private String deptLabel;


    @Excel(name = "账号")
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

    @Excel(name = "姓名")
    @Remark("姓名")
    @Column(length = 50)
    private String name;


    @Excel(name = "电话")
    @Remark("电话")
    @Column(length = 11)
    private String phone;

    @Excel(name = "邮箱")
    @Remark("邮箱")
    @Column(length = 30)
    private String email;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled;





    // 扩展字段1
    private String extra1;
    private String extra2;
    private String extra3;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
    Set<SysRole> roles = new HashSet<>();


    @Excel(name = "角色")
    @Transient
    List<String> roleNames;

    @Transient
    List<String> roleIds;

    // 数据权限类型
    @Enumerated(EnumType.STRING)
    DataPermType dataPermType;

    @JsonIgnore
    @Lazy
    @ManyToMany
    @JoinTable(name = "sys_user_data_perm", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "org_id"))
    List<SysOrg> dataPerms = new ArrayList<>();

    @Override
    public void prePersistOrUpdate() {
        super.prePersistOrUpdate();
        if (dataPermType == null) {
            dataPermType = DataPermType.CHILDREN;
        }
    }

    @Override
    public String toString() {
        return name + " " + phone;
    }


}
