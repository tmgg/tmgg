
package io.tmgg.lang.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
 *
 * 由于命名太长，请使用ExcelWrap
 */
@Deprecated
@Slf4j
public class ExcelOptWrapper {


    Sheet sheet;

    public ExcelOptWrapper(Sheet sheet) {
        this.sheet = sheet;
    }

    public ExcelOptWrapper() {
        this.sheet = new XSSFWorkbook().createSheet();
    }


    public <T> void appendBeanList(Collection<T> list, Column<T>[] columns) {
        Object[] headers = Arrays.stream(columns).map(Column::getTitle).toArray();
        appendRow(headers);

        // 设置列宽
        for (int i = 0; i < columns.length; i++) {
            Column<T> column = columns[i];
            int width = column.getChars() * 255;

            if(width > 0){
                sheet.setColumnWidth(i, width);
            }
        }


        for (Object bean : list) {
            List<Object> rowData = new ArrayList<>(columns.length);
            for (Column col : columns) {
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
            appendRow(rowData);
        }
    }


    public void appendRow(Object[] rowData) {
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

    public void appendRow(List<Object> rowData) {
        appendRow(rowData.toArray());
    }

    public void appendRows(List<List<Object>> rowDataList) {
        for (List<Object> rowData : rowDataList) {
            appendRow(rowData);
        }
    }


    public void writeAndClose(OutputStream os) throws IOException {
        sheet.getWorkbook().write(os);
        sheet.getWorkbook().close();
    }


    @Data
    public static class Column<T> {
        String title;
        String dataIndex; // 支持小数点 如 "org.name"
        Function<T, Object> render;

        int chars; // 字符数，汉字算2个

        public Column(String title, Function<T, Object> render) {
            this.title = title;
            this.render = render;
        }

        public Column(String title, String dataIndex) {
            this.title = title;
            this.dataIndex = dataIndex;
        }

        public Column(int chars,String title, Function<T, Object> render) {
            this(title,render);
            this.chars = chars;
        }

        public Column(int chars,String title, String dataIndex) {
           this(title,dataIndex);
           this.chars = chars;
        }
    }


}
