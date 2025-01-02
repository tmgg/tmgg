package com.xxx.car.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("车辆")
@Getter
@Setter
@Entity
@FieldNameConstants
public class Car extends BaseEntity {

    public Car(String id) {
        super(id);
    }
    public Car() {
        super();
    }

    // 车牌号
    @Remark("车牌号")
    @Column(unique = true)
    String carNo;

    @Remark("车型")
    String type;

    @Remark("颜色")
    String color;

    @Remark("可容纳人数")
    Integer capacity;

    @Remark("启用")
    @Column(nullable = false, columnDefinition = DBConstants.COLUMN_DEFINITION_BOOLEAN_DEFAULT_TRUE)
    private Boolean enabled;


}
