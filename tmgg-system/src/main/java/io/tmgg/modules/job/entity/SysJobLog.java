package io.tmgg.modules.job.entity;

import io.tmgg.web.persistence.BaseEntity;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import io.tmgg.web.persistence.DBConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
public class SysJobLog extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    SysJob sysJob;

    Date beginTime;

    Date endTime;

    @Column(columnDefinition = DBConstants.TYPE_BLOB)
    String result;

    Long jobRunTime;


    @Transient
    public String getJobRunTimeLabel() {
        if (jobRunTime != null) {
            String str = DateUtil.formatBetween(jobRunTime, BetweenFormatter.Level.SECOND);
            return str;
        }
        return null;
    }


}
