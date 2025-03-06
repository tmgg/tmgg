package com.xxx.car.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("车辆")
@Getter
@Setter
@Entity
@FieldNameConstants
public class Car extends BaseEntity {

    @Msg("车牌号")
    @Column(unique = true)
    String carNo;

    @Msg("车型")
    String type;

    @Msg("颜色")
    String color;

    @Msg("可容纳人数")
    Integer capacity;

    @Msg("启用")
    @Column(nullable = false, columnDefinition = DBConstants.COLUMN_DEFINITION_BOOLEAN_DEFAULT_TRUE)
    private Boolean enabled;

}
