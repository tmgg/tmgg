
package io.tmgg.modules.sys.entity;

import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;


/**
 * 参数配置
 */
@Slf4j
@Remark("系统配置")
@Getter
@Setter
@Entity
@FieldNameConstants
@ToString
public class SysConfig extends BaseEntity {

    @Excel(name = "名称")
    @Remark("名称")
    @Column(length = 30, nullable = false)
    private String label;


    /**
     * 属性值
     */
    @Excel(name = "值")
    @Remark("值")
    private String value;

    @Remark("默认值")
    private String defaultValue;

    @Column(length = 20)
    private String valueType;


    @Remark("备注")
    private String remark;

    private Integer seq;



}
