package io.tmgg.modules.openapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "open_api_resource_return argument")
public class OpenApiResourceArgumentReturn extends BaseEntity {


    @Column(length = DBConstants.LEN_NAME)
    String name;

    @Column(length = DBConstants.LEN_NAME)
    String type;
    Integer len;


    @Column(name = "desc_")
    String desc;


    @Msg("示例")
    String demo;

    Boolean required;

    @JsonIgnore
    @NotNull
    @ManyToOne
    OpenApiResource resource;


}
