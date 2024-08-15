package io.tmgg.web.token;

import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Remark("会话")
@Getter
@Setter
@Entity
@FieldNameConstants
public class SysSession extends BaseEntity {

    @NotNull
    String payload;

    @NotNull
    String tokenMd5;

    @NotNull
    Long expireTime;

}
