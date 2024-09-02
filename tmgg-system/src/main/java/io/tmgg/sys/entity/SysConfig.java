
package io.tmgg.sys.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


/**
 * 参数配置
 */

@Remark("系统配置")
@Getter
@Setter
@Entity
@FieldNameConstants
@ToString
public class SysConfig extends BaseEntity {

    @Remark("名称")
    @Column(length = 30, nullable = false, unique = true)
    private String name;

    @Remark("键")
    @Column(unique = true,name = "key_", length = 30)
    private String key;

    /**
     * 属性值
     */
    @Remark("值")
    private String value;

    @Remark("默认值")
    private String defaultValue;

    @Column(length =  20)
    private String valueType;


    @Remark("备注")
    private String remark;




}
