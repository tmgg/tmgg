package io.tmgg.modules.openapi.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("访客权限")
@Entity
@FieldNameConstants
@Getter
@Setter
@Table(name = "open_api_account_resource", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "resource_id"}))
public class OpenApiAccountResource extends BaseEntity {

    @NotNull
    @ManyToOne
    OpenApiAccount account;



    @NotNull
    @ManyToOne
    OpenApiResource resource;


    @NotNull
    Boolean enable;

}
