package io.tmgg.flowable.listener;


import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessDefinitionRegistry {

    private final Map<String, ProcessDefinition> pool = new HashMap<>();

    public void add(String key, ProcessDefinition flowableListener) {
        Assert.state(!pool.containsKey(key), "流程监听器只能设置一个");
        pool.put(key, flowableListener);
    }

    public ProcessDefinition get(String key) {
        return pool.get(key);
    }
}
