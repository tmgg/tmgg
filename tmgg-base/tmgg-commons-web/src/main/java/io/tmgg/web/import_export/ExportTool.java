package io.tmgg.web.import_export;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import io.tmgg.web.WebConstants;
import io.tmgg.web.import_export.core.FileImportExportHandler;
import io.tmgg.web.import_export.core.impl.ExcelImpl;
import io.tmgg.web.import_export.core.impl.JsonImpl;
import io.tmgg.web.import_export.core.impl.PdfImpl;
import jakarta.persistence.Lob;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static io.tmgg.web.import_export.ExportType.*;

/**
 * 将导入导出操作统一放到这里
 */
@Slf4j
public class ExportTool {

    private static final Map<ExportType, FileImportExportHandler> HANDLER_MAP = new HashMap<>();


    static {
        HANDLER_MAP.put(EXCEL, new ExcelImpl());
        HANDLER_MAP.put(PDF, new PdfImpl());
        HANDLER_MAP.put(JSON, new JsonImpl());
    }

    /**
     * 通过请求头 X-Export-Type， 自动实现导出
     */
    public static <T> AjaxResult autoRender(Page<T> page, Class<T> cls) throws Exception {
        HttpServletRequest request = HttpServletTool.getRequest();
        HttpServletResponse response = HttpServletTool.getResponse();

        String type = request.getHeader(WebConstants.HEADER_EXPORT_TYPE);
        if (StrUtil.isEmpty(type)) {
            return AjaxResult.ok().data(page);
        }

        Msg entityMsg = cls.getAnnotation(Msg.class);

        ExportType exportType = valueOf(type);
        FileImportExportHandler handler = HANDLER_MAP.get(exportType);
        Assert.notNull(handler, "不支持" + exportType + "类型的导出");
        Table<T> tb = getTable(page, cls);


        File file = handler.createFile(tb, entityMsg == null ? null : entityMsg.value());
        handler.exportFile(file, entityMsg == null ? "导出文件" : entityMsg.value(), response);
        FileUtil.del(file);

        return null;
    }


    /**
     * 通过注解@Excel导出
     *
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
     *
     * @param table
     * @param filename
     * @param response
     * @throws IOException
     */
    public static <D> void exportExcel(Table<D> table, String filename, HttpServletResponse response) throws IOException {
        ExcelExportTool.exportTable(filename, table, response);
    }


    @NotNull
    private static <T> Table<T> getTable(Page<T> page, Class<T> cls) {
        Table<T> tb = new Table<>(page.getContent());


        boolean hasExcelAnn = Arrays.stream(cls.getDeclaredFields()).anyMatch(t -> t.isAnnotationPresent(Excel.class));
        if (hasExcelAnn) {
            for (Field f : cls.getDeclaredFields()) {
                if (!f.isAnnotationPresent(Excel.class)) {
                    continue;
                }

                Class<?> type1 = f.getType();
                if (type1.isAssignableFrom(String.class) || type1.isAssignableFrom(Number.class) || type1.isAssignableFrom(Date.class)) {
                    String title = f.getAnnotation(Excel.class).name();
                    tb.addColumn(title, f.getName());
                }
            }
            return tb;
        }


        log.warn("实体上未配置Excel注解，将使用默认导出");

        for (Field f : cls.getDeclaredFields()) {
            if (f.isAnnotationPresent(Lob.class)) {
                continue;
            }

            Class<?> type1 = f.getType();
            if (type1.isAssignableFrom(String.class) || type1.isAssignableFrom(Number.class) || type1.isAssignableFrom(Date.class)) {
                String title = f.isAnnotationPresent(Msg.class) ? f.getAnnotation(Msg.class).value() : f.getName();
                tb.addColumn(title, f.getName());
            }
        }
        return tb;
    }


}
