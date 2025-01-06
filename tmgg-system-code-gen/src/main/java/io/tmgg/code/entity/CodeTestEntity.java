package io.tmgg.code.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Remark("测试实体")
@Entity
@Getter
@Setter
public class CodeTestEntity extends BaseEntity {

    @Remark("姓名")
    String name;

    @Remark("年龄")
    Integer age;

    @Transient
    String ageLabel;

    @Remark("入职时间")
    Date workTime;
}
