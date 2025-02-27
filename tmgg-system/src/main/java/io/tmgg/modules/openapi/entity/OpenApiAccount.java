package io.tmgg.modules.openapi.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.ToListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

@Msg("接口访客")
@Entity
@FieldNameConstants
@Getter
@Setter
public class OpenApiAccount extends BaseEntity {


    @Msg("名称")
    @Column(length = 50)
    private String name;



    @Msg("权限")
    @Column(length = 2000)
    @Convert(converter = ToListConverter.class)
    private List<String> perms;

    @Msg("准入IP")
    private String accessIp;



    @Column(unique = true,length = 32)
    private String appSecret;



    @Msg("状态")
    @NotNull
    private Boolean enable;


    @Msg("有效期")
    private Date endTime;
}
