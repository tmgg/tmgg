package io.tmgg.lang.obj;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 表格，用于导出，前端动态展示表格等
 * 参考了antd的格式
 * 前端也可以使用
 * @param <T>
 */
@Getter
public class Table<T> {


    private List<Column<T>> columns = new ArrayList<>();

    private List<T> dataSource;

    // 分页时，总数
    private Long totalElements;


    public Table(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    public Table(Page<T> page) {
        // 传入的列表可能是不可改变的，这里需要
        this.dataSource = new ArrayList<>(page.getContent());
        this.totalElements = page.getTotalElements();
    }

    public void addColumn(String title, String dataIndex){
        columns.add(new Column<>(title,dataIndex));
    }

    public void addColumn(String title, Function<T,Object> render){
        columns.add(new Column<>(title,render));
    }

    /**
     * title 必填， render和dataIndex二选一
     */
    @Getter
    @Setter
    public static class Column<T> {

        String title;

        String dataIndex;

        Function<T,Object> render ;


        public Column() {
        }

        public Column(String dataIndex) {
            this.dataIndex = dataIndex;
        }

        public Column(String title, String dataIndex) {
            this.dataIndex = dataIndex;
            this.title = title;
        }

        public Column(String title,  Function<T,Object> render) {
            this.title = title;
            this.render = render;
        }

        public String getTitle() {
            return title == null ? dataIndex : title;
        }

    }



}
