package io.tmgg.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SysConfigChangeEvent extends ApplicationEvent {

    public SysConfigChangeEvent(Object source,String key) {
        super(source);
        this.key = key;
    }

    String key;
}
