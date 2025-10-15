package io.tmgg.flowable.listener;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowableListenerRegister {

    String processDefinitionKey();
}
