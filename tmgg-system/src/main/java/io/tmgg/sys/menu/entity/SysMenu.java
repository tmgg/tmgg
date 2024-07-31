
package io.tmgg.sys.menu.entity;

import io.tmgg.lang.TreeDefinition;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.enums.MenuType;
import io.tmgg.web.enums.YesOrNotEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统菜单表
 */
@Entity(name = "sys_menu")
@Getter
@Setter
@FieldNameConstants
@ToString
public class SysMenu extends BaseEntity implements TreeDefinition<SysMenu> {


    @Column(length = DBConstants.LEN_ID)
    private String pid;

    /**
     * 名称
     */
    private String name;


    /**
     * 菜单编码
     * 不填则表示公共，
     * 填了之后，后续按钮权限以此为前缀
     *
     * 比如用户模块， 可以是user, 那按钮权限就是 user:add, user:delete等
     * 也可以是sys:user, 按钮权限就是 sys:user:add, sys:user:delete
     *
     */
    @Column(unique = true, length = 50)
    private String code;


    /**
     * 菜单类型（字典 0目录 1菜单 2按钮）
     */
    private MenuType type;

    /**
     * 图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String router;

    private Boolean iframe;

    /**
     * 权限标识
     */
    @Column(unique = true)
    private String permission;


    /**
     * 应用分类（应用编码）
     */
    @NotNull
    private String application;


    /**
     * 是否可见（Y-是，N-否）
     */
    private String visible;


    /**
     * 排序
     */
    private Integer seq;


    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    private CommonStatus status;

    /**
     * 子节点（表中不存在，用于构造树）
     */
    @Transient
    private List<SysMenu> children;


    private String remark;


    @Override
    public void beforeSaveOrUpdate() {
        permission = StringUtils.defaultIfEmpty(permission, null);
        code = StringUtils.defaultIfEmpty(code, null);
        visible = StringUtils.defaultIfEmpty(visible, YesOrNotEnum.Y.getCode());
        status = ObjectUtils.defaultIfNull(status, CommonStatus.ENABLE);
        type = ObjectUtils.defaultIfNull(type, MenuType.MENU);
    }


}
