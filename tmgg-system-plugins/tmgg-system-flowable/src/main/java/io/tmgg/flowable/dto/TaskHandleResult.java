package io.tmgg.flowable.dto;

import lombok.Getter;

@Getter
public enum TaskHandleResult {

    APPROVE("同意"), REJECT("不同意"), BACK("退回");

    String message;


    TaskHandleResult(String message) {
        this.message = message;
    }
}
