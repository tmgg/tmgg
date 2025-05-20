package io.tmgg.lang.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.FontTool;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.data.Matrix;
import io.tmgg.lang.obj.Table;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.MatrixUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class PdfExportTool<T> {


    private static final Font TITLE_FONT = new Font(getBaseFont(), 18, Font.BOLD, BaseColor.BLUE);
    private static final Font HEADER_FONT = new Font(getBaseFont(), 12, Font.BOLD, BaseColor.WHITE);
    private static final Font CELL_FONT = new Font(getBaseFont(), 10);
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(73, 144, 205);


    /**
     * 使用 itext-asian 的字体不好看，这里使用系统安装的字体
     *
     * @return
     */
    private static BaseFont getBaseFont() {
        try {
            String font = FontTool.getDefaultFontPath();
            Assert.notNull(font, "系统中不存在中文字体" );
            log.debug("使用字体文件 {}",font);



            BaseFont bfChinese = BaseFont.createFont(
                    font,
                    BaseFont.IDENTITY_H,   // 使用 Unicode 编码
                    BaseFont.EMBEDDED); // 是否嵌入字体到pdf
            return bfChinese;
        } catch (Exception e) {
            log.error("获取中文字体失败",e);
            throw new RuntimeException(e);
        }
    }


    Matrix matrix;

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
        List<Object> headers = matrix.get(0);
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

    private PdfPTable createUserTable(List<Object> headers) {
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        return table;
    }

    private void addTableHeader(PdfPTable table, List<Object> headers) {
        for (Object header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header.toString(), HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setPadding(8);
            cell.setBorderWidth(1f);

            table.addCell(cell);
        }
    }



    private void addTableRows(PdfPTable table) {
        for (int i = 1; i < matrix.size(); i++) {
            List<Object> rowData = matrix.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                String v = (String) rowData.get(j);
                String value = v == null ? "" : v;
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
