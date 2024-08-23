
package io.tmgg.lang;

import io.tmgg.web.excel.Excel;
import io.tmgg.web.excel.ExcelTool;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 简单导入导出工具类
 *
 */
@Slf4j
public class ExcelImportExportTool {


    public static <T> List<T> importExcel(InputStream is, Class<T> pojoClass) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(is);

        XSSFSheet sheet = wb.getSheetAt(0);

        List<T> list = importExcel(sheet, pojoClass);
        wb.close();

        return list;
    }

    public static <T> List<T> importExcel(XSSFSheet sheet, Class<T> pojoClass) throws Exception {
        ExcelTool.removeEmptyRows(sheet);         // 删除空行

        // 解析注解
        Map<String, String> labelField = new HashMap<>();
        Field[] declaredFields = FieldUtils.getAllFields(pojoClass);
        for (Field field : declaredFields) {
            Excel ann = field.getAnnotation(Excel.class);
            if (ann != null) {
                labelField.put(ann.name(), field.getName()); //  eg 年龄，age
            }
        }


        XSSFRow header = sheet.getRow(0); // 表头
        Map<Integer, String> indexField = new HashMap<>();
        for (Cell cell : header) {
            int columnIndex = cell.getColumnIndex();
            String label = cell.getStringCellValue();
            if (label != null) {
                label = label.trim();

                if (labelField.containsKey(label)) {
                    indexField.put(columnIndex, labelField.get(label));
                }
            }
        }


        List<T> list = new ArrayList<>(sheet.getLastRowNum() + 1);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // 忽略表头
            }
            T t = pojoClass.getConstructor().newInstance();
            list.add(t);

            for (Cell cell : row) {
                Object cellValue = ExcelTool.getCellValue((XSSFCell) cell);
                if (!StrUtil.isBlankIfStr(cellValue)) {
                    String fieldName = indexField.get(cell.getColumnIndex());
                    if (fieldName != null) {
                        BeanUtil.setFieldValue(t, fieldName, cellValue);
                    }
                }
            }
        }

        return list;
    }

    public static void setExportResponseHeader(String filename, HttpServletResponse response) throws IOException {
        String fileName = URLUtil.encode(filename, StandardCharsets.UTF_8);
        response.reset();
        response.setHeader("Access-Control-Expose-Headers", "content-disposition");
        response.setHeader("content-disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");
        ResponseTool.setCrossDomain(HttpServletTool.getRequest(), response);
    }


    public static void exportExcelResponse(String excelName, Workbook workbook, HttpServletResponse response) throws IOException {
        setExportResponseHeader(excelName, response);
        write(workbook, response.getOutputStream());
    }


    public static void write(Workbook workbook, OutputStream os) throws IOException {
        workbook.write(os);
        os.close();
        workbook.close();
    }

    public static void writeBeanList(Class<?> pojoClass, Collection<?> list, OutputStream os) throws IOException {
        Field[] declaredFields = pojoClass.getDeclaredFields();
        List<Field> titleFields = new ArrayList<>(declaredFields.length);
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            Excel ann = field.getAnnotation(Excel.class);
            if (ann != null) {
                titleFields.add(field);
            }
        }


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();


        int rowIndex = 0;


        // 表头
        {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < titleFields.size(); i++) {
                Field field = titleFields.get(i);
                String title = field.getAnnotation(Excel.class).name();
                row.createCell(i).setCellValue(title);
            }
        }

        // 表体
        for (Object bean : list) {
            Row row = sheet.createRow(rowIndex++);

            for (int i = 0; i < titleFields.size(); i++) {
                Field field = titleFields.get(i);
                Object value = BeanUtil.getFieldValue(bean, field.getName());

                if (value == null) {
                    continue;
                }
                row.createCell(i).setCellValue(value.toString());
            }

        }


        write(workbook, os);
    }


    public static void exportBeanList(String excelName, Class<?> pojoClass, Collection<?> list, HttpServletResponse response) throws IOException {
        setExportResponseHeader(excelName, response);
        writeBeanList(pojoClass, list, response.getOutputStream());
    }


    /**
     * 导出二维数组
     *
     * @param excelName 文件名
     * @param header    表头
     * @param dataList  表体数据 , 二维数组 ， 行， 列
     * @param response  servlet response
     *
     * @throws IOException io异常
     */
    public static void exportExcel(String excelName, List<Object> header, List<List<Object>> dataList, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 表头
        {

            Row row = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                Object col = header.get(i);
                Cell cell = row.createCell(i);
                cell.setCellValue(StrUtil.toStringOrNull(col));
            }
        }

        // 表体

        for (int i = 0; i < dataList.size(); i++) {
            List<Object> dataRow = dataList.get(i);
            Row row = sheet.createRow(i + 1);

            for (int j = 0; j < dataRow.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(StrUtil.toStringOrNull(dataRow.get(j)));
            }
        }


        exportExcelResponse(excelName, workbook, response);
    }


    @Deprecated
    public static void exportExcelWithStream(String excelName, List<Object> header, List<List<Object>> dataList) throws IOException {
        exportExcel(excelName, header, dataList, HttpServletTool.getResponse());
    }

    @Deprecated
    public static void exportExcelWithStream(String excelName, Class<?> pojoClass, Collection<?> list) throws IOException {
        exportBeanList(excelName, pojoClass, list, HttpServletTool.getResponse());
    }

    @Deprecated
    public static void exportExcelWithStream(String fileName, Workbook workbook) throws IOException {
        exportExcelResponse(fileName, workbook, HttpServletTool.getResponse());
    }
}
