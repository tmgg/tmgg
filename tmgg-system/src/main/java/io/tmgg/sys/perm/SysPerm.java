
package io.tmgg.sys.perm;

import io.tmgg.lang.TreeDefinition;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import io.tmgg.web.enums.MenuType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


@Entity
@Remark("系统权限")
@Getter
@Setter
@FieldNameConstants
@ToString
public class SysPerm extends BaseEntity implements TreeDefinition<SysPerm> {


    @Column(length = DBConstants.LEN_ID)
    private String pid;


    @Column(length = 50)
    private String name;


    @Column(length = 5)
    @Enumerated(EnumType.STRING)
    private MenuType type;

    @Column(length = 50)
    private String icon;

    /**
     * 路由地址
     */
    private String path;

    private Boolean iframe;

    /**
     * 权限标识
     */
    @Column(unique = true, nullable = false, length = 30)
    private String perm;

    @NotNull
    private Boolean visible;


    private Integer seq;


    /**
     * 子节点（表中不存在，用于构造树）
     */
    @Transient
    private List<SysPerm> children;


    @Override
    public void beforeSaveOrUpdate() {
        perm = StringUtils.defaultIfEmpty(perm, null);
        visible = ObjectUtils.defaultIfNull(visible, true);
        type = ObjectUtils.defaultIfNull(type, MenuType.MENU);
    }

}
