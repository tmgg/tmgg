package io.tmgg.flowable.listener;


import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessDefinitionRegistry {

    private static final Map<String, ProcessDefinition> LISTENERS = new HashMap<>();

    public void add(String key, ProcessDefinition flowableListener){
        Assert.state(!LISTENERS.containsKey(key), "流程监听器只能设置一个");
        LISTENERS.put(key,flowableListener);
    }

    public ProcessDefinition getListener(String key){
        return LISTENERS.get(key);
    }
}
