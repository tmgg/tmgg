package io.tmgg.modules.api.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("访客权限")
@Entity
@FieldNameConstants
@Getter
@Setter
@Table(name = "sys_api_account_resource", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "resource_id"}))
public class ApiAccountResource extends BaseEntity {

    @NotNull
    @ManyToOne
    ApiAccount account;



    @NotNull
    @ManyToOne
    ApiResource resource;


    @NotNull
    Boolean enable;

}
