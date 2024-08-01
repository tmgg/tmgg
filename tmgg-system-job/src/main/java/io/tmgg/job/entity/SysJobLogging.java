package io.tmgg.job.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@FieldNameConstants
public class SysJobLogging extends BaseEntity {

    @NotNull
    @Column(length = 32)
    String jobId;

    @NotNull
    @Column(length = 32)
    String jogLogId;


    @Lob
    String message;

    @Column(length = 5)
    String level;

    Long timeStamp;

}
