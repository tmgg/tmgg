package io.tmgg.event;

import org.springframework.context.ApplicationEvent;

public class SysConfigChangeEvent extends ApplicationEvent {
    public SysConfigChangeEvent(Object source) {
        super(source);
    }
}
