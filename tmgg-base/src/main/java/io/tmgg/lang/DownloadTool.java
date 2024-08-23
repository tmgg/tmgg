
package io.tmgg.lang;

import io.tmgg.web.context.requestno.RequestNoContext;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.log.Log;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 文件下载工具类
 *
 */
public class DownloadTool {

    private static final Log log = Log.get();


    public static void download(String fileName, byte[] fileBytes, HttpServletResponse response) {
        try {

            setDownloadParam(fileName, "" + fileBytes.length, response);
            IoUtil.write(response.getOutputStream(), true, fileBytes);
        } catch (IOException e) {
            log.error(">>> 下载文件异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
            throw new IllegalStateException("下载文件错误");
        }
    }

    public static void setDownloadParam(String fileName, String contentLength, HttpServletResponse response) {
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLUtil.encode(fileName) + "\"");
        if(contentLength != null){
            response.addHeader("Content-Length", contentLength);
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/octet-stream;charset=UTF-8");
    }

    /**
     * 下载文件
     *
     * @param file     要下载的文件
     * @param response 响应
     */
    public static void download(File file, HttpServletResponse response) {
        // 获取文件字节
        byte[] fileBytes = FileUtil.readBytes(file);
        //获取文件名称
        String fileName;
        fileName = URLUtil.encode(file.getName(), StandardCharsets.UTF_8);
        //下载文件
        download(fileName, fileBytes, response);
    }
}
