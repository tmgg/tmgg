package io.tmgg.event;

import org.springframework.context.ApplicationEvent;

public class SystemDataInitFinishEvent extends ApplicationEvent {
    public SystemDataInitFinishEvent(Object source) {
        super(source);
    }
}
