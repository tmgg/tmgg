package io.tmgg.lang;


import io.tmgg.lang.obj.AjaxResult;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.core.io.ClassPathResource;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResponseTool {

    public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";


    public static void setDownloadHeader(String filename, String contentType, HttpServletResponse response) throws IOException {
        filename = URLUtil.encode(filename, StandardCharsets.UTF_8);

        response.setContentType(contentType +";charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.setHeader("Access-Control-Expose-Headers", "content-disposition");
    }


    public static void responseHtmlBlock(HttpServletResponse response, String title, String content) throws IOException {
        if (content != null) {
            content = removeImgPrefix(content);
        }
        response.setContentType("text/html;charset=utf-8");


        if (content != null && title != null) {
            ClassPathResource resource = new ClassPathResource("h5_template.html");
            InputStream is = resource.getInputStream();
            String h5Template = IOUtils.toString(is, StandardCharsets.UTF_8);

            String html = h5Template.replace("{title}", title).replace("{content}", content);
            response.getWriter().write(html);
        }

    }

    private static String removeImgPrefix(String content) {
        String reg = "(<img.*?)(https?://.*?)(/sysFile/preview.*?>)";

        String result = RegExUtils.replacePattern(content, reg, "$1$3");

        return result;
    }



    public static void setCrossDomain(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,jwt,Authorization");

        response.addHeader("Access-Control-Max-Age", "3600");
    }





    public static void responseExceptionError(HttpServletResponse response,
                                              Integer code,
                                              String message) {
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType(ContentType.JSON.toString());
        AjaxResult result = AjaxResult.err().code(code).msg( message);
        String errorResponseJsonData = JsonTool.toPrettyJsonQuietly(result);
        try {
            response.setStatus(code);
            response.getWriter().write(errorResponseJsonData);
        } catch (Exception e) {
            log.error(e.getClass().getName() + ":" + e.getMessage());
        }

    }


    public static AjaxResult responseDataError(Integer code, String message) {
        AjaxResult ajaxResult = AjaxResult.err().code(code).msg(message);
        return ajaxResult;
    }


}
