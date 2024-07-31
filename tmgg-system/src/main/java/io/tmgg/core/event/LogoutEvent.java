package io.tmgg.core.event;

import org.springframework.context.ApplicationEvent;

public class LogoutEvent extends ApplicationEvent {

    public LogoutEvent(Object source,String userId) {
        super(source);
        this.userId = userId;
    }



    private final String userId;

    public String getUserId() {
        return userId;
    }
}
