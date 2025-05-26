package io.tmgg.web.import_export.core;

import io.tmgg.lang.obj.table.Table;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 导入导出
 */
public interface FileImportExportHandler {


    /**
     * 通过二维数组创建文件
     * @param matrix 二维数组
     * @param title 标题
     */
    public <T> File createFile(Table<T> table, String title) throws Exception;

    public void exportFile(File file, String filename, HttpServletResponse response) throws IOException;


    public default void importFile(InputStream is) {

    }

}
