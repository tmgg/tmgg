
package io.tmgg.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.core.jpa.fill.AutoFillFileUrl;
import io.tmgg.core.jpa.fill.AutoFillFileUrlStrategy;
import io.tmgg.core.jpa.fill.AutoFillOrgLabelStrategy;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.AutoFill;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
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
@Msg("系统用户")
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
    @Msg("所属机构")
    private String unitId;

    @Excel(name = "部门")
    @Transient
    @AutoFill(value = AutoFillOrgLabelStrategy.class)
    private String unitLabel;


    @Msg("所属部门")
    private String deptId;

    @Excel(name = "单位")
    @Transient
    @AutoFill(value = AutoFillOrgLabelStrategy.class)
    private String deptLabel;


    @Excel(name = "账号")
    @Msg("账号")
    @NotNull(message = "账号不能为空")
    @Column(unique = true)
    private String account;

    /**
     * 密码， 转换json时不显示，但可接受前端设置
     */
    @Msg("密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Excel(name = "姓名")
    @Msg("姓名")
    @Column(length = 50)
    private String name;


    @Excel(name = "电话")
    @Msg("电话")
    @Column(length = 11)
    private String phone;

    @Excel(name = "邮箱")
    @Msg("邮箱")
    @Column(length = 30)
    private String email;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled;

    /**
     * 密码错误等
     */

    @Excel(name = "是否锁定")
    private Boolean locked;

    /**
     * 账户锁定时间
     * 当错误尝试达到阈值时记录锁定时间
     */
    private Date lockTime;
    /**
     * 每次密码错误时递增，成功登录时重置为0
     */
    private Integer failedAttempts;



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
