package io.tmgg.job.entity;

import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;

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
