package io.tmgg.web.token;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.web.dbproperties.DbPropertiesDao;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.crypto.symmetric.AES;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TokenManger {

    public static final int TOKEN_VALID_HOURS = 8;


    /**
     * token 的md5 及有效期，
     * 1、不存token本身，防止不被其他人如开发人员或运维人员拿到
     * 2、md5即时被命中，但只影响过期时间
     */
    private static Cache<String, Long> TOKEN_MD5_CACHE = CacheBuilder.newBuilder().expireAfterAccess(TOKEN_VALID_HOURS, TimeUnit.HOURS).build();


    public static final String HEADER_PARAM = "Authorization";
    public static final String URL_PARAM = "token";
    public static final String SESSION_PARAM = "token";

    public static final String COOKIE_PARAM = "ck_token";

    @Resource
    private SysSessionDao dao;

    @Resource
    private DbPropertiesDao dbPropertiesDao;



    private AES aes;

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        JpaQuery<SysSession> q = new JpaQuery<>();
        q.gt(SysSession.Fields.expireTime, System.currentTimeMillis());
        List<SysSession> list = dao.findAll(q);
        for (SysSession session : list) {
            TOKEN_MD5_CACHE.put(session.getTokenMd5(), session.getExpireTime());
        }


        {
            // 初始化AES， 每个项目都是随机的，存储再数据库中
            String key = dbPropertiesDao.findStrByCode("TOKEN_AES_KEY");
            if(key == null || key.isEmpty()){
                key = RandomUtil.randomString(16);
                dbPropertiesDao.save("TOKEN_AES_KEY", key);
            }

            String iv = dbPropertiesDao.findStrByCode("TOKEN_AES_IV");
            if(iv == null || iv.isEmpty()){
                iv = RandomUtil.randomString(16);
                dbPropertiesDao.save("TOKEN_AES_IV", iv);
            }
            aes = new AES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
        }

    }

    public String createToken(String payload) {
        long expireTime = System.currentTimeMillis() + TOKEN_VALID_HOURS * 60 * 60 * 1000;

        Object salt = RandomUtils.nextInt(1, 9999); // 加盐，防止生成结果一样
        String text = salt + "," + payload;

        String token = aes.encryptHex(text);
        String md5 = getMd5(text);

        TOKEN_MD5_CACHE.put(md5, expireTime);

        SysSession sysSession = new SysSession();
        sysSession.setTokenMd5(md5);
        sysSession.setExpireTime(expireTime);
        sysSession.setPayload(payload);
        dao.save(sysSession);
        return token;
    }



    /**
     * @param token
     * @return null if invalid
     */
    public String validate(String token) {
        Assert.state(!isEmptyByWeb(token), "凭证不存在");
        try {
            String text = aes.decryptStr(token);
            int idx = text.indexOf(",");
            String payload = text.substring(idx + 1);

            String md5 = getMd5(text);
            Long time = TOKEN_MD5_CACHE.getIfPresent(md5);
            Assert.notNull(time, "凭证已失效。详情：缓存中没有找该凭证, 缓存数量" + TOKEN_MD5_CACHE.size());

            long now = System.currentTimeMillis();
            Assert.state(time > now, "凭证已失效，有效期为" + DateUtil.formatDate(new Date(time)));
            return payload;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    private String getMd5(String token) {
        return MD5.create().digestHex(token);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(HEADER_PARAM);

        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(URL_PARAM);
        }

        if (StringUtils.isEmpty(token)) {
            // 判断iframe, 如果iframe的src包含jwt。页面ajax请求会带Referer
            String refererUrl = request.getHeader(HttpHeaders.REFERER);
            if (StringUtils.isNotEmpty(refererUrl) && refererUrl.contains(URL_PARAM)) {
                CharSequence tokenParam = UrlQuery.of(refererUrl, StandardCharsets.UTF_8).get(URL_PARAM);
                if (StringUtils.isNotEmpty(tokenParam)) {
                    token = tokenParam.toString();
                }
            }
        }

        HttpSession session = request.getSession();
        if (StringUtils.isEmpty(token)) {
            token = (String) session.getAttribute(SESSION_PARAM);
        }

        return token;
    }

    public boolean isValid(String jwt) {
        try {
            validate(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean isEmptyByWeb(String str) {
        return str == null || str.equals("") || str.equals("null") || str.equals("undefined");
    }


}
