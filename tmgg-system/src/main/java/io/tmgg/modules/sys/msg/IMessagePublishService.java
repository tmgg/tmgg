package io.tmgg.modules.sys.msg;

public interface IMessagePublishService {

    public void publish(String topic, String title, String content);
}
