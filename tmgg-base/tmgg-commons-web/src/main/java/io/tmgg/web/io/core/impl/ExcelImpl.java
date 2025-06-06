package io.tmgg.web.io.core.impl;

import cn.hutool.core.io.FileUtil;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.obj.table.Table;
import io.tmgg.lang.obj.table.TableColumn;
import io.tmgg.web.io.core.FileImportExportHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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

            createTitle(title, table.getColumns().size(), workbook, sheet);

            Row headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
            List<TableColumn<T>> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                TableColumn<T> column = columns.get(i);
                String header = column.getTitle();
                headerRow.createCell(i).setCellValue(header);
            }

            // 表体
            for (T bean : table.getDataSource()) {
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                for (int i = 0; i < columns.size(); i++) {
                    TableColumn<T> column = columns.get(i);
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

    private static void createTitle(String title, int columnSize, Workbook workbook,Sheet sheet) {
        if (title != null) {
            // 创建样式 - 居中
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(title);
            cell.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,columnSize));
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
