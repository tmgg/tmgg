package io.tmgg.modules.api.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("接口访客")
@Entity
@FieldNameConstants
@Getter
@Setter
public class ApiAccount extends BaseEntity {


    @Remark("名称")
    @Column(length = 50)
    private String name;

    @Remark("描述")
    private String remark;

    @Remark("权限")
    private String perms;

    @Remark("准入IP")
    private String accessIp;

    private String appKey;

    private String appSecret;

    @Remark("状态")
    private Boolean enable;


}
