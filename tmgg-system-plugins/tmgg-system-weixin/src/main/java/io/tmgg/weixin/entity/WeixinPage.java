package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Remark;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Remark("微信页面")
@Entity
@Getter
@Setter
@FieldNameConstants
public class WeixinPage extends BaseEntity {


    @Column(length = DBConstants.LEN_ID)
    String appId;

    @Remark("标题")
    @Column(length = 50)
    String title;

    @NotNull
    @Remark("路径")
    String path;

    String root;


    @Remark("全路径")
    String page;



}
