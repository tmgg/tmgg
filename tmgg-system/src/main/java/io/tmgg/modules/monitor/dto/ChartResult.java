package io.tmgg.modules.monitor.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class ChartResult {
    private Date createTime;
    private  String time;
    private double value;

    private List<String> test;
}
