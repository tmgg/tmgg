
package io.tmgg.sys.app.entity;

import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 系统应用表
 */
@Getter
@Setter
@Entity
@FieldNameConstants
@ToString
public class SysApp extends BaseEntity {



    /**
     * 名称
     */
    @Column(unique = true)
    @NotNull
    private String name;

    /**
     * 编码
     */
    @Column(unique = true)
    @NotNull
    private String code;

    /**
     * 是否默认激活（Y-是，N-否）,只能有一个系统默认激活
     * 用户登录后默认展示此系统菜单
     */
    private String active;

    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    private CommonStatus status;

    private Integer seq;

    @Override
    public void prePersist() {
        super.prePersist();
        this.status = CommonStatus.ENABLE;
    }
}
