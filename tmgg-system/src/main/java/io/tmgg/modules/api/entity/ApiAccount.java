package io.tmgg.modules.api.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.validator.ValidateIpv4;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Remark("接口账户")
@Entity
@FieldNameConstants
@Getter
@Setter
@Table(name = "sys_api_account")
public class ApiAccount extends BaseEntity {


    @Remark("名称")
    @Column(length = 50)
    private String name;






    @Remark("准入IP")
    @ValidateIpv4
    private String accessIp;



    @Column(unique = true,length = 32)
    private String appSecret;



    @Remark("状态")
    @NotNull(message = "状态不能为空")
    private Boolean enable;


    @Remark("有效期")
    private Date endTime;




}
