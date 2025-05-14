
package io.tmgg.modules.sys.entity;

import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ann.Msg;
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
@Msg("系统配置")
@Getter
@Setter
@Entity
@FieldNameConstants
@ToString
public class SysConfig extends BaseEntity {

    @Excel(name = "名称")
    @Msg("名称")
    @Column(length = 30, nullable = false)
    private String label;


    /**
     * 属性值
     */
    @Excel(name = "值")
    @Msg("值")
    private String value;

    @Msg("默认值")
    private String defaultValue;

    @Column(length = 20)
    private String valueType;


    @Msg("备注")
    private String remark;

    private Integer seq;



}
