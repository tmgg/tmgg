package io.tmgg.web.db;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Remark("数据记录（用于临时存储，存储杂项等）")
@Getter
@Setter
@FieldNameConstants
@Entity
@Table(name = "sys_db_cache")
public class DbCache extends BaseEntity {

    @NotNull
    @Column(unique = true, length = 100)
    String code;

    @Column(length = 5000)
    @NotNull
    String value;
}
