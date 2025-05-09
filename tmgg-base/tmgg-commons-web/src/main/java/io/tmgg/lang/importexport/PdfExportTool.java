package io.tmgg.lang.importexport;

import cn.hutool.core.bean.BeanUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.ResponseTool;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class PdfExportTool<T> {


    private static final Font TITLE_FONT = new Font(getBaseFont(), 18, Font.BOLD, BaseColor.BLUE);
    private static final Font HEADER_FONT = new Font(getBaseFont(), 12, Font.BOLD, BaseColor.WHITE);
    private static final Font CELL_FONT = new Font(getBaseFont(), 10);
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(73, 144, 205);

    private static BaseFont getBaseFont() {
        try {
            BaseFont bfChinese = BaseFont.createFont(
                    "STSong-Light",    // 字体名称
                    "UniGB-UCS2-H",   // 编码标识
                    BaseFont.NOT_EMBEDDED); // 是否嵌入字体
            return bfChinese;
        } catch (Exception e) {
            log.info("获取中文字体失败");
            throw new RuntimeException(e);
        }
    }

    Class<T> cls;

    List<T> dataList;

    String title;

    HttpServletResponse response;


    /**
     * 导出数据列表为PDF
     */
    public void exportBeanList() throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate()); // 横向页面，更适合多列数据
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());


        document.open();

        // 添加标题
        addTitle(document, title);

        // 添加生成时间
        addGenerationInfo(document);

        // 创建表格
        List<String> headers = getHeaders();
        PdfPTable table = createUserTable(headers);

        // 添加表头
        addTableHeader(table, headers);

        // 添加数据行
        addTableRows(table);

        document.add(table);


        ResponseTool.setDownloadHeader(title + ".pdf", ResponseTool.CONTENT_TYPE_EXCEL, response);

        document.close();
        writer.close();


        response.getOutputStream().close();
    }

    private void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20f);
        document.add(p);
    }

    private void addGenerationInfo(Document document) throws DocumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date());

        Paragraph p = new Paragraph("生成时间: " + dateStr,
                new Font(getBaseFont(), 10, Font.NORMAL, BaseColor.GRAY));
        p.setAlignment(Element.ALIGN_RIGHT);
        p.setSpacingAfter(15f);
        document.add(p);
    }

    private PdfPTable createUserTable(List<String> headers) {
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // 设置列宽比例
        //     float[] columnWidths = {1f, 2f, 3f, 2f, 2f, 1f};
        //   table.setWidths(columnWidths);

        return table;
    }

    private void addTableHeader(PdfPTable table, List<String> headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setPadding(8);
            cell.setBorderWidth(1f);

            table.addCell(cell);
        }
    }

    @NotNull
    private List<String> getHeaders() {
        List<String> headers = new ArrayList<>();

        Field[] declaredFields = cls.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Excel ex = declaredField.getAnnotation(Excel.class);
            if (ex == null) {
                continue;
            }
            String name = ex.name();
            headers.add(name);
        }
        return headers;
    }

    private void addTableRows(PdfPTable table) {
        for (int i = 0; i < dataList.size(); i++) {
            T user = dataList.get(i);

            Field[] declaredFields = cls.getDeclaredFields();

            for (Field declaredField : declaredFields) {
                Excel ex = declaredField.getAnnotation(Excel.class);
                if (ex == null) {
                    continue;
                }


                Object fieldValue = BeanUtil.getFieldValue(user, declaredField.getName());
                String value = fieldValue == null ? "" : fieldValue.toString();

                table.addCell(createCell(value));
            }
        }
    }

    private PdfPCell createCell(String content) {
        return createCell(content, Element.ALIGN_CENTER);
    }

    private PdfPCell createCell(String content, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, CELL_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderWidth(1f);
        return cell;
    }
}
