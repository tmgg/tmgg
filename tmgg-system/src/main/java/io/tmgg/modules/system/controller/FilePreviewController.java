package io.tmgg.modules.system.controller;

import cn.hutool.core.util.URLUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ExceptionToMessageTool;
import io.tmgg.lang.RequestTool;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.entity.SysFile;
import io.tmgg.modules.system.service.SysFileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

@Slf4j
@Controller
public class FilePreviewController {

    private final Set<String> allowedPreviewTypes = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "mp4", "avi", "mov"
    );


    @jakarta.annotation.Resource
    SysFileService sysFileService;

    /**
     * 文件预览入口
     */
    @PublicRequest
    @GetMapping({"preview/{id}", "sysFile/preview/{id}", "sysFile/preview/{id}.{suffix}"})
    public ResponseEntity<StreamingResponseBody> previewFile(@PathVariable String id,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response, @PathVariable(required = false) String suffix) {
        SysFile file = sysFileService.findOne(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        String fileExtension = file.getSuffix();
        if (!allowedPreviewTypes.contains(fileExtension)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            InputStream inputStream = sysFileService.getFileStream(file.getId());

            String rangeHeader = request.getHeader("Range");

            // 支持视频流传输（兼容HTML5 video标签）
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                return handlePartialContent(inputStream, file, rangeHeader);
            }

            MyStreamingResponseBody body = new MyStreamingResponseBody(inputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + URLUtil.encode(id + "." + file.getSuffix()) + "\"")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(body);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * 处理部分内容请求（如视频播放）
     */
    private ResponseEntity<StreamingResponseBody> handlePartialContent(InputStream inputStream, SysFile file, String rangeHeader) {
        long fileSize = file.getSize();
        String[] ranges = rangeHeader.substring(6).split("-");
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;

        if (rangeEnd >= fileSize) {
            rangeEnd = fileSize - 1;
        }

        long contentLength = rangeEnd - rangeStart + 1;

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .body(new MyStreamingResponseBody(inputStream, rangeStart, contentLength));
    }

    @ExceptionHandler(Throwable.class)
    public void throwable(Throwable e, HttpServletRequest request) {
        log.debug("预览连接可能关闭 {}", request.getRequestURI());
    }

    private static class MyStreamingResponseBody implements StreamingResponseBody {


        InputStream inputStream;

        long start;
        long contentLength;

        public MyStreamingResponseBody(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public MyStreamingResponseBody(InputStream inputStream, long start, long contentLength) {
            this.inputStream = inputStream;
            this.start = start;
            this.contentLength = contentLength;
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            try {
                if (start > 0) {
                    // 跳过起始字节
                    IOUtils.skipFully(inputStream, start);
                    // 复制指定范围的字节
                    IOUtils.copyLarge(inputStream, outputStream, 0, contentLength);
                } else {
                    // 复制指定范围的字节
                    IOUtils.copyLarge(inputStream, outputStream);
                }
            } finally {
             //   IOUtils.close(inputStream, outputStream);
            }
        }
    }
}
