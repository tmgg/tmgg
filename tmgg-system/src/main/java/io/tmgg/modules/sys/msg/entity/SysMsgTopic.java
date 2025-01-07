package io.tmgg.modules.sys.msg.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * 消息主题，这里仅为了展示
 */
@Remark("消息主题")
@Entity
@Getter
@Setter
@FieldNameConstants
public class SysMsgTopic extends BaseEntity {

    @Column(length = 50)
    String code;

    String description;

}
