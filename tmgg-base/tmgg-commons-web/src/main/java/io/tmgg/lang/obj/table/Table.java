package io.tmgg.lang.obj.table;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.data.Matrix;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * 表格，用于导出，前端动态展示表格等
 * 参考了antd的格式
 * 前端也可以使用
 *
 * @param <T>
 */
@Getter
@Slf4j
public class Table<T> {


    private List<TableColumn<T>> columns = new ArrayList<>();

    private List<T> dataSource;

    // 分页时，总数
    private Long totalElements;

    @Setter
    private String title;


    public Table(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    public Table(Page<T> page) {
        // 传入的列表可能是不可改变的，这里需要
        this.dataSource = new ArrayList<>(page.getContent());
        this.totalElements = page.getTotalElements();
    }

    public TableColumn<T> addColumn(String title, String dataIndex) {
        TableColumn<T> column = new TableColumn<>(title, dataIndex);
        columns.add(column);
        return column;
    }

    public TableColumn<T> addColumn(String title, Function<T, Object> render) {
        TableColumn<T> column = new TableColumn<>(title, render);
        columns.add(column);
        return column;
    }


    @JsonIgnore
    public Object getColumnValue(TableColumn<T> col, T bean) {
        String dataIndex = col.getDataIndex();
        Function<T, Object> render = col.getRender();
        Object value = null;

        if (render != null) {
            value = render.apply(bean);
        } else if (dataIndex != null) {
            value = BeanUtil.getFieldValue(bean, dataIndex);
        }

        return value;
    }

    /**
     * 获得用于渲染的字符串
     *
     * @param col
     * @param bean
     * @return
     */
    public String getColumnValueFormatted(TableColumn<T> col, T bean) {
        Object v = getColumnValue(col, bean);
        if (v == null) {
            return null;
        }
        if (v instanceof Date d) {
            return DateUtil.formatDateTime(d);
        }


        return v.toString();
    }

    /**
     * 获得用于渲染的二维数组
     *
     * @return
     */
    public Matrix getRenderMatrix() {
        Matrix m = new Matrix(dataSource.size() + 1, columns.size());

        for (int i = 0; i < columns.size(); i++) {
            TableColumn<T> column = columns.get(i);
            m.setValue(0, i, column.getTitle());
        }


        for (int i = 0; i < dataSource.size(); i++) {
            T dataRow = dataSource.get(i);
            for (int j = 0; j < columns.size(); j++) {
                TableColumn<T> column = columns.get(j);
                String columnValue = getColumnValueFormatted(column, dataRow);

                if (columnValue != null) {
                    m.setValue(i + 1, j, columnValue);
                }
            }
        }
        return m;
    }


    public static <T> Table<T> of(List<T> list, Class<T> cls) {
        Table<T> tb = new Table<>(list);


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
                String title = f.isAnnotationPresent(Remark.class) ? f.getAnnotation(Remark.class).value() : f.getName();
                tb.addColumn(title, f.getName());
            }
        }
        return tb;
    }




}
