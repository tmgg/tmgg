package io.tmgg.web.io;

import cn.hutool.core.io.FileUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.obj.table.Table;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonExportTool {


    public static  <T> File createFile(Table<T> table) throws Exception {
        List<T> dataSource = table.getDataSource();
        String json = JsonTool.toPrettyJsonQuietly(dataSource);

        File tempFile = FileUtil.createTempFile();

        FileUtil.writeUtf8String(json,tempFile);


        return tempFile;
    }



    public static void exportFile(File file, String filename, HttpServletResponse response) throws IOException {
        if (!filename.endsWith(".json")) {
            filename += ".json";
        }

        ResponseTool.setDownloadHeader(filename, ResponseTool.CONTENT_TYPE_STREAM, response);


        try (ServletOutputStream os = response.getOutputStream()) {
            FileUtil.writeToStream(file, os);
        }
    }


}
