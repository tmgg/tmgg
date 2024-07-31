
package io.tmgg.sys.consts.entity;

import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;


/**
 * 参数配置
 */
@Getter
@Setter
@Entity(name = "sys_config")
@FieldNameConstants
@ToString
public class SysConfig extends BaseEntity {

    /**
     * 名称
     */
    @Column(length = 30, nullable = false)
    private String name;

    /**
     * 编码
     */
    @Column(unique = true)
    private String code;

    /**
     * 属性值
     */
    private String value;

    private String defaultValue;



    /**
     * 备注
     */
    private String remark;




}
