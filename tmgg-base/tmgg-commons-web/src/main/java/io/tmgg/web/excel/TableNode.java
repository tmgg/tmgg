package io.tmgg.web.excel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// 不含tbody ， table 下直接就是tr列表
@Getter
@Setter
public  class TableNode {

    List<TableAttr> attrs = new ArrayList<>();
    List<TableNode> children = new ArrayList<>();



    String reactStyle;

    String tagName;

    // td
    Object field;
    String text;
    String excelCoords;

    @Override
    public String toString() {
        return "TableNode{" +
                "tagName='" + tagName + '\'' +
                '}';
    }
}
