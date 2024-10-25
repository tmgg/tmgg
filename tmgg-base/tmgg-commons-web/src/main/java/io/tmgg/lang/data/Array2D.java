package io.tmgg.lang.data;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.tmgg.lang.excel.ExcelTool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// 二维数组
public class Array2D extends LinkedList<List<Object>> {

    public Array2D() {

    }

    public Array2D(int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            ArrayList<Object> rowData = new ArrayList<>();
            this.add(rowData);

            for (int c = 0; c < cols; c++) {
                rowData.add(null);
            }
        }
    }


    public int getRowSize() {
        return this.size();
    }

    public int getColSize() {
        return this.get(0).size();
    }


    public Array2D addRows(Array2D data) {
        if (!data.isEmpty()) {
            this.addAll(data);
        }
        return this;
    }

    public void addRow(Object[] data) {
        List<Object> rowData = new ArrayList<>();
        for (Object cellData : data) {
            rowData.add(cellData);
        }
        this.add(rowData);
    }


    @JsonIgnore
    public Object getValue(int row, int col) {
        List<Object> rowData = this.get(row);
        if(rowData == null){
            return null;
        }
        return rowData.get(col);
    }


    @JsonIgnore
    public void setValue(int row, int col, Object value) {
        this.get(row).set(col, value);
    }

    public boolean isRowEmpty(List<Object> row) {
        for (Object cell : row) {
            if (!StrUtil.isBlankIfStr(cell)) {
                return false;
            }
        }
        return true;
    }

    public void removeEmptyRows() {
        this.removeIf(this::isRowEmpty);
    }


    public Array2D clone() {
        int rowSize = this.getRowSize();
        int colSize = getColSize();
        Array2D rs = new Array2D(rowSize, colSize);
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                rs.setValue(i, j , this.getValue(i, j));
            }

        }
        return rs;
    }


    /**
     * 合并模板和填报数据
     *
     * @param inputData
     * @return
     */
    public  void mergeInputData( Map<String, Object> inputData) {
        if (inputData == null) {
            return;
        }

        for (Map.Entry<String, Object> e : inputData.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();

            int colIndex = ExcelTool.coordsToColIndex(key);
            int rowIndex = ExcelTool.coordsToRowIndex(key);
            this.setValue(rowIndex, colIndex, value);
        }

    }


    public List<Array2D> split(int rows){
        List<Array2D> list = new ArrayList<>();

        int total = this.size();
        int splitCount = total / rows;

        for(int i = 0; i < splitCount; i++){
            Array2D item = new Array2D();
            for(int j = 0; j < rows; j++){
                item.add(this.get(i * rows + j));
            }
            list.add(item);
        }

        return list;
    }




}
