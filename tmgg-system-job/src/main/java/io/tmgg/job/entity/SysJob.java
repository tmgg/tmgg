package io.tmgg.job.entity;

import io.tmgg.BasePackage;
import io.tmgg.lang.Entry;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.converter.ToEntryListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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


    // 参数
    @Lob
    @Convert(converter = ToEntryListConverter.class)
    List<Entry> jobData;

    @Column(length = 20)
    String type;

    String description;


    @JsonIgnore
    @Transient
    public Map<String, Object> getJobDataMap() {
        if (jobData != null) {
            Map<String, Object> map = new HashMap<>();
            for (Entry jobDatum : jobData) {
                map.put(jobDatum.getKey(), jobDatum.getValue());
            }
            return map;
        }

        return Collections.emptyMap();
    }


    @JsonIgnore
    @Transient
    public String getTriggerKey() {
        return name + "Trigger";
    }


    @Override
    public void beforeSaveOrUpdate() {
        Assert.state(jobClass.endsWith(JOB_SUFFIX), "必须以" + JOB_SUFFIX + "结尾");
    }
}
