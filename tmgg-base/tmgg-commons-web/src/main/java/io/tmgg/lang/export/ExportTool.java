package io.tmgg.lang.export;


import cn.hutool.core.util.StrUtil;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import io.tmgg.web.WebConstants;
import jakarta.persistence.Lob;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 将导入导出操作统一放到这里
 */
@Slf4j
public class ExportTool {

    /**
     * 通过注解@Excel导出
     * @param list
     * @param filename
     * @param response
     * @throws IOException
     */
    public static <D> void exportExcel(List<D> list, String filename, Class<D> beanCls, HttpServletResponse response) throws IOException {
        ExcelExportTool.exportBeanList(filename, list, beanCls, response);
    }

    /**
     * 自定义导出
     * @param table
     * @param filename
     * @param response
     * @throws IOException
     */
    public static <D> void exportExcel(Table<D> table, String filename, HttpServletResponse response) throws IOException {
        ExcelExportTool.exportTable(filename, table, response);
    }


    /**
     * 通过请求头 X-Export-Type， 自动实现导出
     *
     */
    public static <T> AjaxResult autoRender(Page<T> page, Class<T> cls) throws Exception {
        HttpServletRequest request = HttpServletTool.getRequest();
        HttpServletResponse response = HttpServletTool.getResponse();
        String exportExcel = request.getHeader(WebConstants.HEADER_EXPORT_TYPE);
        if (StrUtil.isEmpty(exportExcel)) {
            return AjaxResult.ok().data(page);
        }

        Msg msg = cls.getAnnotation(Msg.class);
        String filename = msg != null ? msg.value() : "导出文件";

        if (exportExcel.equalsIgnoreCase("Excel")) {
            boolean hasExcelAnn = Arrays.stream(cls.getDeclaredFields()).anyMatch(t -> t.isAnnotationPresent(Excel.class));
            if(hasExcelAnn){
                ExcelExportTool.exportBeanList(filename , page.getContent(), cls, response);
                return null;
            }

                log.warn("实体上未配置Excel注解，将使用默认导出");
                Table<T> tb = new Table<>(page.getContent());
                for (Field f : cls.getDeclaredFields()) {
                    if(f.isAnnotationPresent(Lob.class)){
                        continue;
                    }

                    Class<?> type1 = f.getType();
                    if(type1.isAssignableFrom(String.class) || type1.isAssignableFrom(Number.class) || type1.isAssignableFrom(Date.class)){
                        String title = f.isAnnotationPresent(Msg.class) ? f.getAnnotation(Msg.class).value() : f.getName();
                        tb.addColumn(title, f.getName());
                    }
                }

                ExcelExportTool.exportTable(filename, tb, response);


        }
        if (exportExcel.equalsIgnoreCase("Pdf")) {
            new PdfExportTool<>(cls, page.getContent(), filename, response).exportBeanList();
            return null;
        }


        return null;
    }




}
