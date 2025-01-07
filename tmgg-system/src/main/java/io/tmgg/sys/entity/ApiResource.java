package io.tmgg.sys.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;


@Remark("接口资源")
@Entity
@Getter
@Setter
@FieldNameConstants
public class ApiResource extends BaseEntity {


    @Remark("名称")
    private String name;

    @Remark("资源定位")
    private String url;



    @Remark("描述")
    private String description;

    @Remark("状态")
    private Boolean enable;
}
