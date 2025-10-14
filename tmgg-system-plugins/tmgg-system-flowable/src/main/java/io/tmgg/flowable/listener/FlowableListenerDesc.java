package io.tmgg.flowable.listener;

import jakarta.annotation.Resources;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowableListenerDesc {

    String processDefinitionKey();
}
