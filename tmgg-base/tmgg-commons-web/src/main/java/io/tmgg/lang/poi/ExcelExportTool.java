
package io.tmgg.lang.poi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import io.tmgg.commons.poi.excel.ExcelExportUtil;
import io.tmgg.commons.poi.excel.entity.ExportParams;
import io.tmgg.commons.poi.excel.entity.enmus.ExcelType;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.data.Array2D;
import io.tmgg.lang.obj.Table;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.*;
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
        ExportParams param = new ExportParams();
        param.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(param, pojoClass, list);

        exportWorkbook(filename, workbook, response);
    }

    public static <T> void exportTable(String filename, Table<T> tb, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 表头
        List<Table.Column<T>> columns = tb.getColumns();
        Row firstRow = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < columns.size(); i++) {
            Table.Column<T> col = columns.get(i);
            firstRow.createCell(i).setCellValue(col.getTitle());
        }


        // 表体
        for (T dataRow : tb.getDataSource()) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            for (int i = 0; i < columns.size(); i++) {
                Table.Column<T> column = columns.get(i);
                String dataIndex = column.getDataIndex();
                Function<T,Object> render = column.getRender();
                Object value = null;

                if (render != null) {
                    value = render.apply(dataRow);
                } else if (dataIndex != null) {
                    value = BeanUtil.getFieldValue(dataRow, dataIndex);
                }

                if (value != null) {
                    row.createCell(i).setCellValue(value.toString());
                }
            }
        }

        exportWorkbook(filename, workbook, response);
    }







    public static void exportWorkbook(String filename, Workbook workbook, HttpServletResponse response) throws IOException {
        ResponseTool.setDownloadHeader(filename, ResponseTool.CONTENT_TYPE_EXCEL, response);

        workbook.write(response.getOutputStream());
        response.getOutputStream().close();
        workbook.close();
    }
}
