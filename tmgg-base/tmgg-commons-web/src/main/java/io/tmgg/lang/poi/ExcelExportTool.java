
package io.tmgg.lang.poi;

import cn.hutool.core.util.ObjUtil;
import io.tmgg.commons.poi.excel.ExcelExportUtil;
import io.tmgg.commons.poi.excel.entity.ExportParams;
import io.tmgg.commons.poi.excel.entity.enmus.ExcelType;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.data.Array2D;
import cn.hutool.core.bean.BeanUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * 简单导入导出工具类
 */
@Slf4j
public class ExcelExportTool {

    /**
     *  通过Excel 注解导出
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
            int i = 0;
            for (Map.Entry<String, Function<T, Object>> e : columns.entrySet()) {
                Function<T, Object> fn = e.getValue();
                Object value = fn.apply(bean);
                value = ObjUtil.defaultIfNull(value, "");

                row.createCell(i).setCellValue(value.toString());
                i++;
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

    /**
     * 导出map列表， map的key就是表头，可使用中文
     * @param filename
     * @param maplist
     * @param response
     * @throws IOException
     */
    public static void exportMapList(String filename, List<Map<String,Object>> maplist, HttpServletResponse response) throws IOException {
        Assert.notEmpty(maplist,"数据不能为空"); // 为空时无法获取表头
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 表头
        Map<String, Object> first = maplist.get(0);
        Set<String> titles = first.keySet();
        Row firstRow = sheet.createRow(sheet.getLastRowNum() + 1);
        for (String title : titles) {
            firstRow.createCell(firstRow.getLastCellNum()).setCellValue(title);
        }


        // 表体
        for (Map<String, Object> dataRow : maplist) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            for (Map.Entry<String, Object> e : dataRow.entrySet()) {
                Object value = e.getValue();
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
