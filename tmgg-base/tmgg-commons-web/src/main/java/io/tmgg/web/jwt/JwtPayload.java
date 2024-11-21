package io.tmgg.web.jwt;


import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 详细信息请参考：
 * https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-32#section-4.1.1
 */
@Data
public class JwtPayload {

    private static final long ONE_DAY = 1000L * 60 * 60 * 24;


    private String iss; // 签发者, 可用通过不同的签发者，存放不同的sub， 比如微信可能存的是openid
    private String sub; // Subject , 用户，会员等, 这里推荐只存id，至于说对用户的经常读取，可以使用缓存

    private String aud; // String or Url， 比如，某个token 之允许在某些url下，或某些场景下使用

    private long exp; // (expires): 什么时候过期，这里是一个Unix时间戳
    private long iat; // (issued at): 在什么时候签发的


    public JwtPayload() {
        this.iat = System.currentTimeMillis();

        this.setExp(this.iat + (ONE_DAY * 15));
    }

    public void setExp(long expireTime) {
        this.exp = expireTime;
        // 如果过期时间超过一天， 则讲过期时间设置为零点
        if (exp - iat >= ONE_DAY) {
            exp = DateUtils.truncate(new Date(exp), Calendar.DATE).getTime();
        }
    }


}
