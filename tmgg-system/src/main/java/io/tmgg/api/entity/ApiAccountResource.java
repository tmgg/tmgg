package io.tmgg.api.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;

import java.util.Date;

@Remark("接口授权")
@Entity
@FieldNameConstants
@Getter
@Setter
public class ApiAccountResource extends BaseEntity {


    @Remark("准入IP")
    private String accessIp;

    @Remark("超期时间")
    private Date endDate;



    @Remark("资源定位")
    @ManyToOne
    private ApiResource apiResource;


    @Remark("访问者")
    @ManyToOne
    private ApiAccount visitor;

    @Remark("状态")
    private Boolean enable;

}
