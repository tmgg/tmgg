package io.tmgg.modules.monitor.entity;

import cn.hutool.core.date.DateUtil;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SysMetricRecord extends BaseEntity {


    // 指标名称：hikari.active.connections, jvm.memory.used等
    @Column(length = 100)
    String metricName;


    @Column
    private Double metricValue; // 指标数值



    @Column(length = 10)
    String date;

    @Override
    public void prePersist() {
        super.prePersist();
        date = DateUtil.formatDate(this.getCreateTime());
    }
}
