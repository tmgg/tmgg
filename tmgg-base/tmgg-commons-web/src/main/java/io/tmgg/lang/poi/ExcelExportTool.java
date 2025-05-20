
package io.tmgg.lang.poi;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.commons.poi.excel.ExcelExportUtil;
import io.tmgg.commons.poi.excel.entity.ExportParams;
import io.tmgg.commons.poi.excel.entity.enmus.ExcelType;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.data.Matrix;
import io.tmgg.lang.obj.Table;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 简单导入导出工具类
 * <p>
 * 注意：
 * 列范围：0..16383
 */
@Slf4j
public class ExcelExportTool {

    /**
     * 通过Excel 注解导出
     *
     * @param filename
     * @param pojoClass
     * @param list
     * @param response
     * @throws IOException
     */
    public static void exportBeanList(String filename, Collection<?> list, Class<?> pojoClass, HttpServletResponse response) throws IOException {
        list = new ArrayList<>(list);
        ExportParams param = new ExportParams();
        param.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(param, pojoClass, list);

        exportWorkbook(filename, workbook, response);
    }

    public static <T> void exportTable(String filename, Table<T> tb, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 表体
        Matrix renderMatrix = tb.getRenderMatrix();
        for (int i = 0; i < renderMatrix.size(); i++) {
            List<Object> rowData = renderMatrix.get(i);

            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int j = 0; j < rowData.size(); j++) {
                Object value = rowData.get(j);
                if (value != null) {
                    row.createCell(i).setCellValue(value.toString());
                }
            }
        }

        exportWorkbook(filename, workbook, response);
    }


    public static void exportWorkbook(String filename, Workbook workbook, HttpServletResponse response) throws IOException {
        if (!filename.endsWith(".xlsx")) {
            filename += ".xlsx";
        }
        ResponseTool.setDownloadHeader(filename, ResponseTool.CONTENT_TYPE_EXCEL, response);

        workbook.write(response.getOutputStream());
        response.getOutputStream().close();
        workbook.close();
    }
}
