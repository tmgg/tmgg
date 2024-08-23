package io.tmgg.web.dbproperties;

import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@FieldNameConstants
@Entity
@Table(name = "sys_db_properties")
public class DbProperties extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String code;

    @NotNull
    String value;
}
