package io.tmgg.job.entity;

import io.tmgg.BasePackage;
import io.tmgg.lang.Entry;
import io.tmgg.lang.JsonTool;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.ToEntryListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.dao.converter.ToMapConverter;
import io.tmgg.lang.dao.converter.ToMapStringObjectConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.Assert;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@Entity
@FieldNameConstants
public class SysJob extends BaseEntity {

    public SysJob() {

    }

    public SysJob(String id) {
        this.id = id;
    }

    public static final String JOB_SUFFIX = "Job";

    @Column(unique = true)
    @NotNull
    String name;

    @NotNull
    String cron;


    @NotNull
    Boolean enabled;


    @NotNull
    String jobClass;

    @Column(name = "group_")
    String group;


    // 参数
    @Column(length = 20000)
    @Convert(converter = ToMapConverter.class)
    Map<String,Object> jobData;


    // 扩展字段
    String extraInfo;


    @JsonIgnore
    @Transient
    public Map<String, Object> getJobDataMap() {
        if (jobData != null) {
            return jobData;
        }

        return Collections.emptyMap();
    }


    @JsonIgnore
    @Transient
    public String getTriggerKey() {
        return name + "Trigger";
    }


    @Override
    public void prePersistOrUpdate() {
        Assert.state(jobClass.endsWith(JOB_SUFFIX), "必须以" + JOB_SUFFIX + "结尾");
    }
}
