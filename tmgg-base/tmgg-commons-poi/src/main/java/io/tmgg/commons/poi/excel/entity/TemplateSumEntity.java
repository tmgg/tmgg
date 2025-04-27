package io.tmgg.commons.poi.excel.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计对象
 *
 * @author JueYue
 */
@Data
public class TemplateSumEntity {

    /**
     * CELL的值
     */
    private String cellValue;
    /**
     * 需要计算的KEY
     */
    private String sumKey;
    /**
     * 列
     */
    private int    col;
    /**
     * 行
     */
    private int    row;
    /**
     * 最后值
     */
    private BigDecimal value;

    public BigDecimal getValue() {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return value;
    }

}
