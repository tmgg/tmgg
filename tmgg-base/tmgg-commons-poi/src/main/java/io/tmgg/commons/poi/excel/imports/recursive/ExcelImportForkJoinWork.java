package io.tmgg.commons.poi.excel.imports.recursive;

import io.tmgg.commons.poi.excel.entity.ImportParams;
import io.tmgg.commons.poi.excel.entity.params.ExcelImportEntity;
import io.tmgg.commons.poi.excel.entity.result.ExcelImportResult;
import io.tmgg.commons.poi.excel.imports.ExcelImportService;
import io.tmgg.commons.poi.exception.excel.ExcelImportException;
import io.tmgg.commons.poi.exception.excel.enums.ExcelImportEnum;
import io.tmgg.commons.poi.handler.inter.IExcelDataModel;
import io.tmgg.commons.poi.util.PoiPublicUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

/**
 * 并行导入计算
 * 支持校验
 * 不支持图片
 * 不支持集合
 *
 * @author by jueyue on 19-6-9.
 */
public class ExcelImportForkJoinWork extends RecursiveTask<ExcelImportResult> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExcelImportForkJoinWork.class);

    private int                            startRow;
    private int                            endRow;
    private Sheet                          sheet;
    private ImportParams                   params;
    private Class<?>                       pojoClass;
    private ExcelImportService             importService;
    private String                         targetId;
    private Map<Integer, String>           titlemap;
    private Map<String, ExcelImportEntity> excelParams;

    public ExcelImportForkJoinWork(int startRow, int endRow, Sheet sheet, ImportParams params,
                                   Class<?> pojoClass, ExcelImportService importService, String targetId,
                                   Map<Integer, String> titlemap, Map<String, ExcelImportEntity> excelParams) {
        this.startRow = startRow;
        this.endRow = endRow;
        this.sheet = sheet;
        this.params = params;
        this.pojoClass = pojoClass;
        this.importService = importService;
        this.targetId = targetId;
        this.titlemap = titlemap;
        this.excelParams = excelParams;
    }

    @Override
    protected ExcelImportResult compute() {
        long              length = endRow - startRow;
        ExcelImportResult result = null;
        if (length <= params.getCritical()) {
            LOGGER.debug("excel import concurrent task start {} , end {}", startRow, endRow);
            return readRow();
        } else {
            int                     middle = (startRow + endRow) / 2;
            ExcelImportForkJoinWork right  = new ExcelImportForkJoinWork(startRow, middle, sheet, params, pojoClass, importService, targetId, titlemap, excelParams);
            right.fork();
            ExcelImportForkJoinWork left = new ExcelImportForkJoinWork(middle + 1, endRow, sheet, params, pojoClass, importService, targetId, titlemap, excelParams);
            left.fork();
            //合并
            result = right.join();
            ExcelImportResult leftResult = left.join();
            result.getList().addAll(leftResult.getList());
            result.getFailList().addAll(leftResult.getFailList());
        }
        return result;
    }

    private ExcelImportResult readRow() {
        StringBuilder     errorMsg;
        Row               row;
        Object            object;
        ExcelImportResult result = new ExcelImportResult();
        result.setFailList(new ArrayList());
        result.setList(new ArrayList());
        boolean isMap = Map.class.equals(pojoClass);
        for (int i = startRow; i <= endRow; i++) {
            row = sheet.getRow(i);
            errorMsg = new StringBuilder();
            if (params.getKeyIndex() != null && (row.getCell(params.getKeyIndex()) == null)) {
                continue;
            }
            object = PoiPublicUtil.createObject(pojoClass, targetId);
            try {
                Set<Integer> keys = titlemap.keySet();
                for (Integer cn : keys) {
                    Cell   cell        = row.getCell(cn);
                    String titleString = (String) titlemap.get(cn);
                    if (excelParams.containsKey(titleString) || isMap) {
                        try {
                            importService.saveFieldValue(params, object, cell, excelParams, titleString, row);
                        } catch (ExcelImportException e) {
                            // 如果需要去校验就忽略,这个错误,继续执行
                            if (params.isNeedVerify() && ExcelImportEnum.GET_VALUE_ERROR.equals(e.getType())) {
                                errorMsg.append(" ").append(titleString).append(ExcelImportEnum.GET_VALUE_ERROR.getMsg());
                            }
                        }
                    }
                }
                if (object instanceof IExcelDataModel) {
                    ((IExcelDataModel) object).setRowNum(row.getRowNum());
                }
                if (importService.verifyingDataValidity(object, row, params, isMap, errorMsg)) {
                    result.getList().add(object);
                } else {
                    result.getFailList().add(object);
                }
            } catch (ExcelImportException e) {
                LOGGER.error("excel import error , row num:{},obj:{}", i, ReflectionToStringBuilder.toString(object));
                if (!e.getType().equals(ExcelImportEnum.VERIFY_ERROR)) {
                    throw new ExcelImportException(e.getType(), e);
                }
            } catch (Exception e) {
                LOGGER.error("excel import error , row num:{},obj:{}", i, ReflectionToStringBuilder.toString(object));
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
