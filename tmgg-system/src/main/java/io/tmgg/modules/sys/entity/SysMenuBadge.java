package io.tmgg.modules.sys.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Msg("菜单小红点")
@Getter
@Setter
@Entity
public class SysMenuBadge extends BaseEntity {

    @Column(length = DBConstants.LEN_ID)
    @NotNull
    @Msg("菜单ID")
    String menuId;

    @NotNull
    @Msg("请求地址")
    String url;

    @Transient
    String menuName;
}
