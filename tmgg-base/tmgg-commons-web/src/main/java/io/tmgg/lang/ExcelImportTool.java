package io.tmgg.lang;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.web.excel.Excel;
import io.tmgg.web.excel.ExcelTool;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelImportTool {
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
}
