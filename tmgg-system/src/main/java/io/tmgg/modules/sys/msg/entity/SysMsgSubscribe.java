package io.tmgg.modules.sys.msg.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Msg("消息订阅")
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
