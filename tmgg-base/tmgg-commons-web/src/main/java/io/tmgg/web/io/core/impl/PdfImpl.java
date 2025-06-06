package io.tmgg.web.io.core.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.tmgg.lang.FontTool;
import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.obj.table.Table;
import io.tmgg.lang.obj.table.TableColumn;
import io.tmgg.web.io.core.FileImportExportHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class PdfImpl implements FileImportExportHandler {
    private  final Font TITLE_FONT;
    private  final Font HEADER_FONT;
    private  final Font CELL_FONT;
    private  final BaseColor HEADER_BG_COLOR;

    public PdfImpl() {
        TITLE_FONT = new Font(getBaseFont(), 18, Font.BOLD, BaseColor.BLUE);
        HEADER_FONT = new Font(getBaseFont(), 12, Font.BOLD, BaseColor.WHITE);
        CELL_FONT = new Font(getBaseFont(), 10);
        HEADER_BG_COLOR = new BaseColor(73, 144, 205);
    }


    @Override
    public <T> File createFile(Table<T> tb, String title) throws Exception {
        Document document = new Document(PageSize.A4.rotate()); // 横向页面，更适合多列数据

        File tempFile = FileUtil.createTempFile();

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile));



        document.open();

        // 添加标题
        addTitle(document, title);

        // 添加生成时间
        addGenerationInfo(document);

        List<TableColumn<T>> columns = tb.getColumns();

        // 创建表格

        PdfPTable table = createUserTable(columns.size());

        // 添加表头
        addTableHeader(table, columns);


        // 添加数据行
        addTableRows(table, tb);

        document.add(table);


        document.close();
        writer.close();
        return tempFile;
    }



    @Override
    public void exportFile(File file, String filename, HttpServletResponse response) throws IOException {
        if (!filename.endsWith(".pdf")) {
            filename += ".pdf";
        }

        ResponseTool.setDownloadHeader(filename, ResponseTool.CONTENT_TYPE_PDF, response);


        try (ServletOutputStream os = response.getOutputStream()) {
            FileUtil.writeToStream(file, os);
        }
    }




    /**
     * 使用 itext-asian 的字体不好看，这里使用系统安装的字体
     *
     * @return
     */
    private static BaseFont getBaseFont() {
        try {
            String font = FontTool.getDefaultFontPath();
            log.warn(font, "【警告】系统中不存在中文字体，导出PDF可能会出现中文乱码");
            if(font == null){
                return null;
            }
            log.debug("使用字体文件 {}", font);

            BaseFont bfChinese = BaseFont.createFont(
                    font,
                    BaseFont.IDENTITY_H,   // 使用 Unicode 编码
                    BaseFont.EMBEDDED); // 是否嵌入字体到pdf
            return bfChinese;
        } catch (Exception e) {
            log.error("获取中文字体失败", e);
            throw new RuntimeException(e);
        }
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

    private PdfPTable createUserTable(int width) {
        PdfPTable table = new PdfPTable(width);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        return table;
    }

    private <T> void addTableHeader(PdfPTable table,     List<TableColumn<T>> columns) {
        for (TableColumn<T> column : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(column.getTitle(), HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setPadding(8);
            cell.setBorderWidth(1f);

            table.addCell(cell);
        }
    }


    private <T> void addTableRows(PdfPTable table,  Table<T> tb) {
        for (T bean : tb.getDataSource()) {
            for (TableColumn<T> column : tb.getColumns()) {
                String s = tb.getColumnValueFormatted(column, bean);
                table.addCell(createCell(s));
            }
        }
    }

    private PdfPCell createCell(String content) {
        if(content == null){
            content = StrUtil.EMPTY;
        }
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
