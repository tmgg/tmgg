package io.tmgg.sys.msg.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.user.entity.SysUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Remark("消息订阅")
@Getter
@Setter
@Entity
@FieldNameConstants
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_sys_msg_sub",columnNames = {"topic", "sysUserId"}))
public class SysMsgSubscribe extends BaseEntity {


    @NotNull
    @Column(length = 50)
    String topic;


    @Column(length = 32)
    String sysUserId;
}
