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

    String result;

    // 是否成功
    Boolean success;

    Long jobRunTime;

    @Column(length = 10)
    String executeDate;


    @Transient
    public String getJobRunTimeLabel() {
        if (jobRunTime != null) {
            String str = DateUtil.formatBetween(jobRunTime, BetweenFormatter.Level.SECOND);
            return str;
        }
        return null;
    }

    @Override
    public void prePersist() {
        super.prePersist();
        this.executeDate = DateUtil.formatDate(beginTime);
    }
}
