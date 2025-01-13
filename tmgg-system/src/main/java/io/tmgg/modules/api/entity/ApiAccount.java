package io.tmgg.modules.api.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("接口访客")
@Entity
@FieldNameConstants
@Getter
@Setter
public class ApiAccount extends BaseEntity {


    @Msg("名称")
    @Column(length = 50)
    private String name;

    @Msg("描述")
    private String remark;

    @Msg("权限")
    private String perms;

    @Msg("准入IP")
    private String accessIp;

    private String appKey;

    private String appSecret;

    @Msg("状态")
    private Boolean enable;


}
