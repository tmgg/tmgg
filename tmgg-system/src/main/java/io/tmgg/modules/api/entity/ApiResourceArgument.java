package io.tmgg.modules.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
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
@Table(name = "sys_api_resource_argument")
public class ApiResourceArgument extends BaseEntity {

    @NotNull
            @Column(length = DBConstants.LEN_NAME)
    String name;

    @NotNull
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
    ApiResource resource;

    @NotNull
    @Column(name = "index_")
    Integer index;
}
