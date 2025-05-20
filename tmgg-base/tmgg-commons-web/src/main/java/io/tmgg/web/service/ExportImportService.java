package io.tmgg.web.service;


import cn.hutool.core.util.StrUtil;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.importexport.PdfExportTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import jakarta.persistence.Lob;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ExportImportService {

    /**
     * 通过注解@Excel导出
     * @param list
     * @param filename
     * @param response
     * @throws IOException
     */
    public <D> void exportExcel(List<D> list, String filename, Class<D> beanCls,HttpServletResponse response) throws IOException {
        ExcelExportTool.exportBeanList(filename, list, beanCls, response);
    }

    /**
     * 自定义导出
     * @param table
     * @param filename
     * @param response
     * @throws IOException
     */
    public <D> void exportExcel(Table<D> table, String filename, HttpServletResponse response) throws IOException {
        ExcelExportTool.exportTable(filename, table, response);
    }


    /**
     * 通过请求头 X-Export-Type， 自动实现导出
     *
     * @param page
     * @return
     * @param <D>
     * @throws IOException
     */
    public <D> AjaxResult autoRender(Page<D> page) throws Exception {
        HttpServletRequest request = HttpServletTool.getRequest();
        HttpServletResponse response = HttpServletTool.getResponse();
        String exportExcel = request.getHeader("X-Export-Type");
        if (StrUtil.isEmpty(exportExcel)) {
            return AjaxResult.ok().data(page);
        }

        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Class<D> cls = (Class<D>) type;

        Msg msg = cls.getAnnotation(Msg.class);
        String filename = msg != null ? msg.value() : cls.getSimpleName();

        if (exportExcel.equalsIgnoreCase("Excel")) {
            boolean hasExcelAnn = Arrays.stream(cls.getDeclaredFields()).anyMatch(t -> t.isAnnotationPresent(Excel.class));
            if(hasExcelAnn){
                ExcelExportTool.exportBeanList(filename , page.getContent(), cls, response);
                return null;
            }
            boolean hasMsgAnn = Arrays.stream(cls.getDeclaredFields()).anyMatch(t -> t.isAnnotationPresent(Msg.class));
            if(!hasMsgAnn){
                log.warn("实体上未配置Excel注解，将使用@Msg注解，不推荐，请修改。如果不想导出，请在前端配置");
                Table<D> tb = new Table<>(page.getContent());
                for (Field f : cls.getDeclaredFields()) {
                    if(f.isAnnotationPresent(Lob.class)){
                        continue;
                    }
                    if(!f.isAnnotationPresent(Msg.class)){
                        continue;
                    }

                    Class<?> type1 = f.getType();
                    if(type1.isAssignableFrom(String.class) || type1.isAssignableFrom(Number.class) || type1.isAssignableFrom(Date.class)){
                        tb.addColumn(f.getAnnotation(Msg.class).value(), f.getName());
                    }
                }

                ExcelExportTool.exportTable(filename, tb, response);

            }

        }
        if (exportExcel.equalsIgnoreCase("Pdf")) {
            new PdfExportTool<>(cls, page.getContent(), filename, response).exportBeanList();
            return null;
        }


        return null;
    }


}
