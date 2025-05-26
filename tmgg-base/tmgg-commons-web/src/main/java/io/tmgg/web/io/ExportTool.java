package io.tmgg.web.io;


import cn.hutool.core.io.FileUtil;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.obj.table.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import io.tmgg.web.WebConstants;
import io.tmgg.web.io.core.FileImportExportHandler;
import io.tmgg.web.io.core.impl.ExcelImpl;
import io.tmgg.web.io.core.impl.JsonImpl;
import io.tmgg.web.io.core.impl.PdfImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.tmgg.web.io.ExportType.*;

/**
 * 将导入导出操作统一放到这里
 */
public class ExportTool {

    private static final Map<ExportType, FileImportExportHandler> HANDLER_MAP = new HashMap<>();


    static {
        HANDLER_MAP.put(EXCEL, new ExcelImpl());
        HANDLER_MAP.put(PDF, new PdfImpl());
        HANDLER_MAP.put(JSON, new JsonImpl());
    }


    public static boolean isExportRequest(){
        return getExportType() != null;

    }

    public static ExportType getExportType(){
        HttpServletRequest request = HttpServletTool.getRequest();


        String type = request.getHeader(WebConstants.HEADER_EXPORT_TYPE);
        if(type == null){
            return null;
        }
       return ExportType.valueOf(type);

    }

    /**
     * 通过请求头 X-Export-Type， 自动实现导出
     */
    public static <T> void export(List<T> data, Class<T> cls) throws Exception {
        HttpServletRequest request = HttpServletTool.getRequest();
        String type = request.getHeader(WebConstants.HEADER_EXPORT_TYPE);
        Assert.hasText(type,"导出类型"+WebConstants.HEADER_EXPORT_TYPE+"不能为空");

        Msg entityMsg = cls.getAnnotation(Msg.class);


        Table<T> tb = Table.of(data, cls);
        tb.setTitle(entityMsg == null ? null : entityMsg.value());

        export(tb, entityMsg == null ? "导出文件" : entityMsg.value());
    }

    public static <T> void export(Table<T> tb, String filename) throws Exception {
        ExportType exportType = getExportType();
        FileImportExportHandler handler = HANDLER_MAP.get(exportType);
        Assert.notNull(handler, "不支持" + exportType + "类型的导出");

        File file = handler.createFile(tb, filename);


        HttpServletResponse response = HttpServletTool.getResponse();
        handler.exportFile(file, filename, response);
        FileUtil.del(file);
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





}
