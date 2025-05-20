package io.tmgg.web.import_export.core.impl;

import cn.hutool.core.io.FileUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.data.Matrix;
import io.tmgg.lang.obj.Table;
import io.tmgg.web.import_export.core.FileImportExportHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JsonImpl implements FileImportExportHandler {


    @Override
    public <T> File createFile(Table<T> table, String title) throws Exception {
        List<T> dataSource = table.getDataSource();
        String json = JsonTool.toPrettyJsonQuietly(dataSource);

        File tempFile = FileUtil.createTempFile();

        FileUtil.writeUtf8String(json,tempFile);


        return tempFile;
    }



    @Override
    public void exportFile(File file, String filename, HttpServletResponse response) throws IOException {
        if (!filename.endsWith(".json")) {
            filename += ".json";
        }

        ResponseTool.setDownloadHeader(filename, ResponseTool.CONTENT_TYPE_STREAM, response);


        try (ServletOutputStream os = response.getOutputStream()) {
            FileUtil.writeToStream(file, os);
        }
    }

    @Override
    public void importFile(InputStream is) {
        FileImportExportHandler.super.importFile(is);
    }
}
