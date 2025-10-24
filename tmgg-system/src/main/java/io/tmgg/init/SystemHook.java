package io.tmgg.init;

public interface SystemHook {


    @Deprecated
    default void beforeDataInit() {

    }


    @Deprecated
    default void afterDataInit() {

    }

    default void onEvent(SystemHookEventType eventType){

    }
}
