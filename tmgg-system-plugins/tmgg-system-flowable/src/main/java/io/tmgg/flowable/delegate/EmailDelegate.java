package io.tmgg.flowable.delegate;

import io.tmgg.lang.ann.Remark;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Remark("发送邮件")
public class EmailDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("模拟发送邮件");
    }
}
