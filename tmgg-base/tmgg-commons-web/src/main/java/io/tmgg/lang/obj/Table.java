package io.tmgg.lang.obj;


import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
public class Table<T> {

    private List<Column<T>> columns = new ArrayList<>();

    private List<T> dataSource = new ArrayList<>();

    public void addColumn(String dataIndex, String title){
        columns.add(new Column(dataIndex,title));
    }

    public void addColumn(String dataIndex, Function<T,Object> render){
        columns.add(new Column(dataIndex,render));
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

        public Column(String dataIndex, String title) {
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
