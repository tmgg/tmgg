package io.tmgg.modules.system.entity;

import io.tmgg.lang.ann.Remark;

@Remark("数据权限类型")
public enum DataPermType  {

    @Remark("所有")
    ALL,

    @Remark("本级")
    LEVEL,

    @Remark("本级和子级")
    CHILDREN,


    @Remark("自定义")
    CUSTOM;

}
