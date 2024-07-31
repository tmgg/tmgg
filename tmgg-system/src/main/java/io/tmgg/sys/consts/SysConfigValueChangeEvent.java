package io.tmgg.sys.consts;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
@ToString
public class SysConfigValueChangeEvent extends ApplicationEvent {

    public SysConfigValueChangeEvent(Object source) {
        super(source);
    }

    String code;
    String oldValue;
    String newValue;
}
