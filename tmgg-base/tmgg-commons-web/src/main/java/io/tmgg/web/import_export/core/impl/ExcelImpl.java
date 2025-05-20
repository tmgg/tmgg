package io.tmgg.web.import_export.core.impl;

import cn.hutool.core.io.FileUtil;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.obj.Table;
import io.tmgg.web.import_export.core.FileImportExportHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelImpl implements FileImportExportHandler {

    @Override
    public <T> File createFile(Table<T> table, String title) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            if (title != null) {
                Row row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue(title);
            }

            Row headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
            List<Table.Column<T>> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                Table.Column<T> column = columns.get(i);
                String header = column.getTitle();
                headerRow.createCell(i).setCellValue(header);
            }

            // 表体
            for (T bean : table.getDataSource()) {
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                for (int i = 0; i < columns.size(); i++) {
                    Table.Column<T> column = columns.get(i);
                    String v = table.getColumnValueFormatted(column, bean);
                    if(v != null){
                        row.createCell(i).setCellValue(v);
                    }
                }
            }



            File tempFile = FileUtil.createTempFile();


            try (FileOutputStream os = new FileOutputStream(tempFile)) {
                workbook.write(os);
            }
            return tempFile;
        }
    }



    @Override
    public void exportFile(File tempFile, String filename,HttpServletResponse response) throws IOException {
        if (!filename.endsWith(".xlsx")) {
            filename += ".xlsx";
        }
        ResponseTool.setDownloadHeader(filename, ResponseTool.CONTENT_TYPE_EXCEL, response);

        try (ServletOutputStream os = response.getOutputStream()) {
            FileUtil.writeToStream(tempFile, os);
        }
    }
}
