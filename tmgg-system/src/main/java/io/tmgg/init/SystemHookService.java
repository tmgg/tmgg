package io.tmgg.init;

import io.tmgg.lang.SpringTool;
import org.springframework.stereotype.Component;

@Component
public class SystemHookService {

    public void trigger(SystemHookEventType type){
        for (SystemHook hook : SpringTool.getBeans(SystemHook.class)) {
            hook.onEvent(type);
        }
    }
}
