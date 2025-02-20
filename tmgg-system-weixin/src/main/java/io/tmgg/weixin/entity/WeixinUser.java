package io.tmgg.weixin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.dao.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@FieldNameConstants
@ToString
public class WeixinUser extends BaseEntity {

    @Column(length = 32)
    @Msg("应用ID")
    String appId;

    @Column(length = 32)
    String openId;

    @JsonIgnore
    String sessionKey;

    @Column(length = 32)
    String unionId;

    Date lastLoginTime;


    String nickName; //		是	用户昵称

    @Column(length = 1000)
    String avatarUrl;

    String phone;
}
