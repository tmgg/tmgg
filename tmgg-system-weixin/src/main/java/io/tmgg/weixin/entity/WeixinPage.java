package io.tmgg.weixin.entity;

import io.tmgg.lang.ann.Msg;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.DBConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Msg("微信页面")
@Entity
@Getter
@Setter
@FieldNameConstants
public class WeixinPage extends BaseEntity {


    @Column(length = DBConstants.LEN_ID)
    String appId;

    @Msg("标题")
    @Column(length = 50)
    String title;

    @NotNull
    @Msg("路径")
    String path;

    String root;


    @Msg("全路径")
    String page;



}
