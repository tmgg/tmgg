package io.tmgg.modules.chart;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class QueryData {

    String[] keys;

    Map<String, Object[]> data = new HashMap<>();

    List<Map<String, Object>> listData;
}
