
package io.tmgg.lang.excel;

import cn.hutool.core.util.ObjUtil;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.data.Array2D;
import io.tmgg.web.excel.Excel;
import cn.hutool.core.bean.BeanUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * 简单导入导出工具类
 */
@Slf4j
public class ExcelExportTool {

    public static void exportBeanListByAnn(String filename, Class<?> pojoClass, Collection<?> list, HttpServletResponse response) throws IOException {
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


        exportWorkbook(filename, workbook, response);
    }


    /**
     * 导出表格

     List<SysUser> list = sysUserService.findAll();


     Map<String, Function<SysUser,Object>> columns = new LinkedHashMap<>();
     columns.put("姓名", SysUser::getName);
     columns.put("账号", SysUser::getAccount);
     columns.put("手机号", SysUser::getPhone);
     columns.put("部门", SysUser::getDeptLabel);
     columns.put("单位",SysUser::getUnitLabel);
     columns.put("角色", SysUser::getRoleNames);

     ExcelExportTool.exportBeanList("用户列表.xlsx",  list,columns, response);

     * @param filename
     * @param response
     * @throws IOException
     */
    public static <T> void exportBeanList(String filename, List<T> dataList, LinkedHashMap<String, Function<T, Object>> columns, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();


        // 表头
        {
            Row row = sheet.createRow(0);

            Set<String> keys = columns.keySet();
            int i = 0;
            for (String col : keys) {
                row.createCell(i).setCellValue(col);
                i++;
            }
        }


        // 表体
        for (T bean : dataList) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            for (Map.Entry<String, Function<T, Object>> e : columns.entrySet()) {
                Function<T, Object> fn = e.getValue();
                Object value = fn.apply(bean);
                value = ObjUtil.defaultIfNull(value, "");

                row.createCell(row.getLastCellNum()).setCellValue(value.toString());
            }

        }


        exportWorkbook(filename, workbook, response);
    }

    /**
     * 导出表格（二维数组）
     *
     * @param filename
     * @param response
     * @throws IOException
     */
    public static void exportArray2D(String filename, Array2D array2D, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        for (List<Object> rows : array2D) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            for (Object value : rows) {
                value = ObjUtil.defaultIfNull(value, "");
                row.createCell(row.getLastCellNum()).setCellValue(value.toString());
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
