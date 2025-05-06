package io.tmgg.modules.openapi.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("接口访问记录")
@Entity
@FieldNameConstants
@Getter
@Setter
public class OpenApiAccessLog extends BaseEntity {

    @Msg("接口名称")
    @Column(length = 100)
    private String name;

    @Msg("接口")
    @Column(length = 30)
    private String action;

    @Msg("请求数据")
    @Lob
    private String requestData;

    @Msg("响应数据")
    @Lob
    private String responseData;

    @Column(length = 15)
    private String ip;

    @Column(length = 100)
    private String ipLocation;

    @Msg("执行时间")
    private Long executionTime;

    @Msg("接口账户")
    private String accountName;


}
