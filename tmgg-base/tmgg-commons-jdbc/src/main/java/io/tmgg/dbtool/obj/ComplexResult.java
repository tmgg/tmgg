package io.tmgg.dbtool.obj;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ComplexResult {
    private  String[] keys;

    private   List<Map<String,Object>> dataList;

   private Map<String, List<Object>> keyedMapList = new HashMap<>();


}
