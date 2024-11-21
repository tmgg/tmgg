package io.tmgg.web.jwt;

import cn.crec.lang.RequestTool;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

public class JwtTool {


    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PARAM = "jwt";
    public static final String JWT_SESSION = "jwt";

    public static JwtPayload getValidPayload(HttpServletRequest request) throws JwtNotFountException, JwtInvalidException, JwtExpireException {
        String jwt = getJwt(request);
        return getValidPayload(jwt);
    }


    public static JwtPayload getValidPayload(String jwt) {
        if (isEmptyByWeb(jwt)) {
            throw new JwtNotFountException();
        }

        return Jwt.validate(jwt);
    }


    public static String getJwt(HttpServletRequest request) {
        String jwt = request.getHeader(JWT_HEADER);




        if(StringUtils.isEmpty(jwt)){
            jwt = request.getParameter(JWT_PARAM);
        }

        if(StringUtils.isEmpty(jwt)){
            // 判断iframe, 如果iframe的src包含jwt。页面ajax请求会带Referer
            String refererUrl = request.getHeader(HttpHeaders.REFERER);
            if(StringUtils.isNotEmpty(refererUrl) && refererUrl.contains(JWT_PARAM)){
                CharSequence jwtSequence = UrlQuery.of(refererUrl, StandardCharsets.UTF_8).get(JWT_PARAM);
                if(StringUtils.isNotEmpty(jwtSequence)){
                    jwt= jwtSequence.toString();
                }
            }
        }

        HttpSession session = request.getSession();
        if(StringUtils.isEmpty(jwt)){
            jwt = (String) session.getAttribute(JWT_SESSION);
        }

        // 针对那种后端集成的框架，如ureport,就讲jwt放到session中
        if(StringUtils.isNotEmpty(jwt)){
            session.setAttribute(JWT_SESSION, jwt);
        }

        return jwt;
    }
    public static boolean isValid(String jwt) {
        try {
            Jwt.validate(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }



    public static boolean isValid(HttpServletRequest request) {
        try {
            getValidPayload(request);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static boolean isEmptyByWeb(String str) {
        if (str == null) {
            return true;
        }

        if (str.equals("null")) {
            return true;
        }

        if (str.equals("undifined")) {
            return true;
        }
        if (str.equals("")) {
            return true;
        }

        return false;

    }
}
