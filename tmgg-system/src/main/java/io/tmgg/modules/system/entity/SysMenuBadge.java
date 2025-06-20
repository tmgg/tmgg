package io.tmgg.modules.system.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Remark("菜单小红点")
@Getter
@Setter
@Entity
public class SysMenuBadge extends BaseEntity {

    @Column(length = DBConstants.LEN_ID)
    @NotNull
    @Remark("菜单ID")
    String menuId;

    @NotNull
    @Remark("请求地址")
    String url;

    @Transient
    String menuName;
}
