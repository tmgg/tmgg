package io.tmgg.modules.code.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Msg("测试实体")
@Entity
@Getter
@Setter
public class CodeTestEntity extends BaseEntity {

    @Msg("姓名")
    String name;

    @Msg("年龄")
    Integer age;

    @Transient
    String ageLabel;

    @Msg("入职时间")
    Date workTime;
}
