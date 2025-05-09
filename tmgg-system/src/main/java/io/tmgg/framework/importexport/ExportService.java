package io.tmgg.framework.importexport;

import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.obj.Table;
import io.tmgg.lang.poi.ExcelExportTool;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
public class ExportService {

    public void exportExcel(Table<T> tb,String filename) throws IOException {
        ExcelExportTool.exportTable(filename, tb, HttpServletTool.getResponse());
    }


    public void exportPdf(Table<T> tb,String filename){

    }
}
