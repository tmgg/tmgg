package io.tmgg.sys.user;

import org.springframework.context.ApplicationEvent;

public class UserChangedEvent extends ApplicationEvent {

    public UserChangedEvent(Object source) {
        super(source);
    }

}


