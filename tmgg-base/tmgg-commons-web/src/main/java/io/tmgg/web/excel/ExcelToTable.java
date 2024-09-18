package io.tmgg.web.excel;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ExcelToTable {

    public static final String NEXT = "";

    XSSFSheet sheet;

    int maxColIndex;
    int maxRowIndex;

    public static final String DIR_LEFT = "left";
    public static final String DIR_RIGHT = "right";
    public static final String DIR_TOP = "top";
    public static final String DIR_BOTTOM = "bottom";

    public ExcelToTable(XSSFSheet sheet) {
        this.sheet = sheet;

        this.maxColIndex = ExcelTool.getMaxCol(sheet);
        this.maxRowIndex = ExcelTool.getMaxRow(sheet);
    }



    org.jsoup.nodes.Document doc;

    public void parse() throws Exception {
        doc = Jsoup.parse("<table><tbody></tbody></table>");


        doc.attr("width", getTableWidth() + "px");
        doc.attr("width", getTableWidth() + "px");
        doc.attr("style", "border-collapse:collapse;text-align:center");
        doc.attr("class", "table-form");


        for (int i = 0; i <= maxRowIndex; i++) {

            Element tr = doc.createElement("tr");
            doc.selectFirst("tbody").appendChild(tr);

            Integer rowHeight = ExcelTool.getRowHeight(sheet.getRow(i));
            if (rowHeight != null) {
                tr.attr("style", "height:" + rowHeight + "px");
            }

            for (int j = 0; j <= maxColIndex; j++) {
                Element td = doc.createElement("td");
                td.attr("id", ExcelTool.indexToCoords(i, j));
                tr.appendChild(td);
            }
        }
        System.out.println(toHTML());

        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();


            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                XSSFCell cell = (XSSFCell) cellIterator.next();

                int rowIndex = cell.getRowIndex();
                int colIndex = cell.getColumnIndex();

                String coords = ExcelTool.indexToCoords(rowIndex, colIndex);
                Element td = doc.getElementById(coords);
                processCell(td, cell);
            }
        }


        // 处理合并单元格
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            int firstRow = mergedRegion.getFirstRow();
            int firstColumn = mergedRegion.getFirstColumn();
            int lastColumn = mergedRegion.getLastColumn();
            int lastRow = mergedRegion.getLastRow();

            int colspan = lastColumn - firstColumn + 1;
            int rowSpan = lastRow - firstRow + 1;

            for (int r = firstRow; r <= lastRow; r++) {
                for (int c = firstColumn; c <= lastColumn; c++) {
                    Element td = doc.getElementById(ExcelTool.indexToCoords(r, c));
                    td.attr("merged", "true");
                }
            }

            {
                Element td = doc.getElementById(ExcelTool.indexToCoords(firstRow, firstColumn));
                if (td != null) {
                    if (colspan > 1) {
                        td.attr("colspan", String.valueOf(colspan));
                    }
                    if (rowSpan > 1) {
                        td.attr("rowspan", String.valueOf(rowSpan));
                    }
                }

            }
        }

        Elements deletedList = doc.select("[merged]");

        for (Element td : deletedList) {
            if (td.hasAttr("colspan") || td.hasAttr("rowspan")) {
                td.removeAttr("merged");
            } else {
                // real delete
                td.remove();
            }
        }


    }


    private void processCell(Element td, XSSFCell cell) {
        if (cell == null) {
            // 此处填空
            td.appendChild(doc.appendText("CELL不存在"));
            return;
        }


        td.attr("data-excel-coords", ExcelTool.getCoords(cell));

        String cellValue = getCellValue(cell);

        td.appendText(cellValue);

        XSSFCellStyle cellStyle = cell.getCellStyle();


        HorizontalAlignment alignmentEnum = cellStyle.getAlignment();
        if (alignmentEnum != HorizontalAlignment.GENERAL) {
            td.attr("align", alignmentEnum.name().toLowerCase());
        }


        String style = "width:" + ExcelTool.getColWidth(sheet, cell.getColumnIndex()) + "px;";

        {
            // 字体
            XSSFFont font = cell.getCellStyle().getFont();

            String fontName = font.getFontName();
            boolean bold = font.getBold();
            short color = font.getColor();

            short fontHeight = font.getFontHeight();

            int fontSize = fontHeight / 15;


            style += "font-size:" + fontSize + "px;";
            style += "font-family:" + fontName + ";";
            if (bold) {
                style += "font-weight:bold;";
            }

        }

        {
            // 文字垂直排列 255
            short rotation = cellStyle.getRotation();
            if (rotation == 255) {
                style += "writing-mode: vertical-lr;";
            }
        }

        {
            // 边框
            style += getBorder(DIR_LEFT, cell);
            style += getBorder(DIR_RIGHT, cell);
            style += getBorder(DIR_TOP, cell);
            style += getBorder(DIR_BOTTOM, cell);
        }

        // 默认padding
        style += "padding-left: 4px";


        td.attr("style", style);
    }


    private String getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            default:
                return "错误";
        }
    }

    private String getBorder(String direction, XSSFCell cell) {
        XSSFCellStyle cellStyle = cell.getCellStyle();
        BorderStyle borderStyle = null;

        // 合并单元格，则取最后一个格子的 右边，下边
        XSSFCell lastCell = getLastCellIfInMerge(cell);
        if (lastCell != null && (direction.equals(DIR_RIGHT) || direction.equals(DIR_BOTTOM))) {
            cellStyle = lastCell.getCellStyle();
        }


        switch (direction) {
            case DIR_LEFT:
                borderStyle = cellStyle.getBorderLeft();
                break;
            case DIR_TOP:
                borderStyle = cellStyle.getBorderTop();
                break;
            case DIR_RIGHT:
                borderStyle = cellStyle.getBorderRight();
                break;
            case DIR_BOTTOM:
                borderStyle = cellStyle.getBorderBottom();
                break;
            default:
                throw new IllegalArgumentException("方向错误" + direction);
        }


        if (borderStyle == BorderStyle.THIN) {
            return "border-" + direction + ": 1px solid black;";
        }

        if (borderStyle == BorderStyle.MEDIUM) {
            return "border-" + direction + ": 2px solid black;";
        }


        return "";
    }

    /**
     * 如果单元格是合并单元格， 获取最后一个单元格，方便查询样式
     *
     * @param cell
     *
     */
    private XSSFCell getLastCellIfInMerge(XSSFCell cell) {
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        int r = cell.getRowIndex();
        int c = cell.getColumnIndex();
        for (CellRangeAddress address : mergedRegions) {
            int r1 = address.getFirstRow();
            int r2 = address.getLastRow();
            int c1 = address.getFirstColumn();
            int c2 = address.getLastColumn();
            boolean in = r >= r1 && r <= r2 && c >= c1 && c <= c2;
            if (in) {
                XSSFRow row = sheet.getRow(r2);
                if (row != null) {
                    return row.getCell(c2);
                }
            }
        }

        return null;
    }


    public String toHTML() throws IOException {

        return doc.body().html();
    }


    private String getRectStyle(Element el) {
        // 调整style
        String style = el.attr("style");
        if (StrUtil.isEmpty(style)) {
            return "";
        }


        StringBuilder sb = new StringBuilder();
        sb.append("style={{");
        String[] ss = style.split(";");
        for (String s : ss) {
            String[] kv = s.split(":");
            String k = kv[0];
            k = k.replaceAll("-", "_");
            k = StrUtil.lowerFirst(StrUtil.toCamelCase(k));

            sb.append(k).append(":");
            String v = kv[1];


            if (v.endsWith("px")) {
                v = v.replace("px", "");
                sb.append(v);
            } else {
                sb.append("'").append(v).append("'");
            }
            sb.append(",");
        }
        if (ss.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("}}");

        return sb.toString();
    }




    private int getTableWidth() {
        int width = 0;
        for (int i = 0; i <= maxColIndex; i++) {
            int colWidth = ExcelTool.poiWidthToPixels(sheet.getColumnWidth(i));
            width = width + colWidth;
        }
        return width;
    }


}

