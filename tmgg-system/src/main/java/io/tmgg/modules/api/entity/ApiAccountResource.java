package io.tmgg.modules.api.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;

import java.util.Date;

@Msg("接口授权")
@Entity
@FieldNameConstants
@Getter
@Setter
public class ApiAccountResource extends BaseEntity {


    @Msg("准入IP")
    private String accessIp;

    @Msg("超期时间")
    private Date endDate;



    @Msg("资源定位")
    @ManyToOne
    private ApiResource apiResource;


    @Msg("访问者")
    @ManyToOne
    private ApiAccount visitor;

    @Msg("状态")
    private Boolean enable;

}
