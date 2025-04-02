package io.tmgg.lang.obj;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Table {

    private List<Column> columns = new ArrayList<>();

    private List<Map<String, Object>> dataSource = new ArrayList<>();

    public void addColumn(String dataIndex, String title){
        columns.add(new Column(dataIndex,title));
    }

    @Getter
    @Setter
    public static class Column {
        String dataIndex;
        String title;

        public Column() {
        }

        public Column(String dataIndex) {
            this.dataIndex = dataIndex;
        }

        public Column(String dataIndex, String title) {
            this.dataIndex = dataIndex;
            this.title = title;
        }
    }
}
