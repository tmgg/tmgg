package io.tmgg.lang.dao;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IpValidator implements ConstraintValidator<IpValid,String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value != null && !value.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            // 返回判断信息
            // 返回判断信息
            return value.matches(regex);
        }
        return false;



    }
}
