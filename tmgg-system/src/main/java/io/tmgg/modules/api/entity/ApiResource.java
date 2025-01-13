package io.tmgg.modules.api.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;


@Msg("接口资源")
@Entity
@Getter
@Setter
@FieldNameConstants
public class ApiResource extends BaseEntity {


    @Msg("名称")
    private String name;

    @Msg("资源定位")
    private String url;



    @Msg("描述")
    private String description;

    @Msg("状态")
    private Boolean enable;
}
