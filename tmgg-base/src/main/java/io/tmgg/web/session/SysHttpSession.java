package io.tmgg.web.session;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Remark("http会话")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysHttpSession extends BaseEntity {

    @Lob
    @NotNull
    @Column(columnDefinition = "blob")
    byte[] payload;


}
