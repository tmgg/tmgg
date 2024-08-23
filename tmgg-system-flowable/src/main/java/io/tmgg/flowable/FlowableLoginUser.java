package io.tmgg.flowable;


import lombok.Data;

@Data
public class FlowableLoginUser {
    String id;
    String name;

    String unitId;
    String unitName;
    String deptId;
    String deptName;


    boolean superAdmin;
}
