package io.tmgg.web.persistence.converter;

import io.tmgg.web.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum UserType implements CodeEnum {

    simple(1,"普通用户"),
    admin(2,"管理员");


    int code;
    String msg;

}
