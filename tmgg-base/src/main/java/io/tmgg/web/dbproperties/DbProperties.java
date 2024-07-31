package io.tmgg.web.dbproperties;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
