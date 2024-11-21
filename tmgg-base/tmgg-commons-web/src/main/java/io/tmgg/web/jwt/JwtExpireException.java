package io.tmgg.web.jwt;


import org.apache.commons.lang3.time.DateFormatUtils;

public class JwtExpireException extends IllegalStateException {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    String currentTime;
    String expireTime;

    String message;

    public JwtExpireException(JwtPayload payload) {
        super("jwt过期");
        currentTime = formatTime(System.currentTimeMillis());
        expireTime = formatTime(payload.getExp());

        message = String.format("登录过期,当前时间%s,过期时间%s,创建事件", currentTime, expireTime, formatTime(payload.getIat()));
    }

    private String formatTime(long t) {
        return DateFormatUtils.format(t, YYYY_MM_DD_HH_MM_SS);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
