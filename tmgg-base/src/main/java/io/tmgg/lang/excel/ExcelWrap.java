
package io.tmgg.lang.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Excel包装类，方便操作Excel
 * 参考用户导出 SysUserController.export
 */
@Slf4j
public class ExcelWrap {


    Sheet sheet;

    public ExcelWrap(Sheet sheet) {
        this.sheet = sheet;
    }

    public ExcelWrap() {
        this.sheet = new XSSFWorkbook().createSheet();
    }


    public <T> void addBeanList(Collection<T> list, Col<T>[] cols) {
        Object[] headers = Arrays.stream(cols).map(Col::getTitle).toArray();
        addRow(headers);

        // 设置列宽
        for (int i = 0; i < cols.length; i++) {
            Col<T> col = cols[i];
            int width = col.getChars() * 255;

            if(width > 0){
                sheet.setColumnWidth(i, width);
            }
        }


        for (Object bean : list) {
            List<Object> rowData = new ArrayList<>(cols.length);
            for (Col col : cols) {
                Object value = null;
                String dataIndex = col.getDataIndex();
                Function render = col.getRender();

                if (dataIndex != null) {
                    String[] arr = dataIndex.split("\\.");

                    value = bean;
                    for (int i = 0; i < arr.length; i++) {
                        String key = arr[i];
                        value = BeanUtil.getFieldValue(value, key);
                        if (value == null) {
                            break;
                        }
                    }

                }


                if (render != null) {
                    value = render.apply(bean);
                }

                rowData.add(value);
            }
            addRow(rowData);
        }
    }


    public void addRow(Object[] rowData) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < rowData.length; i++) {
            Object value = rowData[i];
            Cell cell = row.createCell(i);
            setValue(cell, value);
        }
    }

    public void setValue(Cell cell, Object value) {
        if (value != null) {
            if (value instanceof Float) {
                cell.setCellValue((Float) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof BigDecimal) {
                cell.setCellValue( ((BigDecimal) value).doubleValue());
            } else {
                cell.setCellValue(StrUtil.toStringOrNull(value));
            }


        }
    }

    public void addRow(List<Object> rowData) {
        addRow(rowData.toArray());
    }

    public void addRows(List<List<Object>> rowDataList) {
        for (List<Object> rowData : rowDataList) {
            addRow(rowData);
        }
    }


    public void writeTo(OutputStream os) throws IOException {
        sheet.getWorkbook().write(os);
        sheet.getWorkbook().close();
    }


    public Workbook getWorkbook(){
        return sheet.getWorkbook();
    }




}
