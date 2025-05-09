package io.tmgg.lang.importexport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import io.tmgg.commons.poi.excel.annotation.Excel;
import io.tmgg.lang.DownloadTool;
import io.tmgg.lang.ResponseTool;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class PdfExportTool<T> {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font CELL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(70, 130, 180);
    private static final BaseColor ROW_BG_COLOR = new BaseColor(240, 240, 240);

    Class<T> cls;

    List<T> dataList;

    String title;

    HttpServletResponse response;





    /**
     * 导出数据列表为PDF
     */
    public void exportBeanList()
            throws DocumentException, IOException, IllegalAccessException {

        Document document = new Document(PageSize.A4.rotate()); // 横向页面，更适合多列数据
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        // 设置文档属性
        document.addTitle("数据列表报表");
        document.addAuthor("系统自动生成");
        document.addCreator("企业应用系统");

        document.open();

        // 添加标题
        addTitle(document, title);

        // 添加生成时间
        addGenerationInfo(document);

        // 创建表格
        PdfPTable table = createUserTable();

        // 添加表头
        addUserTableHeader(table);

        // 添加数据行
        addUserTableRows(table);

        document.add(table);


        ResponseTool.setDownloadHeader(title +".pdf", ResponseTool.CONTENT_TYPE_EXCEL, response);

        document.close();
        writer.close();


        response.getOutputStream().close();
    }

    private  void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20f);
        document.add(p);
    }

    private  void addGenerationInfo(Document document) throws DocumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date());

        Paragraph p = new Paragraph("生成时间: " + dateStr,
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY));
        p.setAlignment(Element.ALIGN_RIGHT);
        p.setSpacingAfter(15f);
        document.add(p);
    }

    private  PdfPTable createUserTable() throws DocumentException {
        // 6列：ID, 数据名, 邮箱, 部门, 创建时间, 状态
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // 设置列宽比例
        float[] columnWidths = {1f, 2f, 3f, 2f, 2f, 1f};
        table.setWidths(columnWidths);

        return table;
    }

    private  void addUserTableHeader(PdfPTable table) {
        List<String> headers = new ArrayList<>();

        Field[] declaredFields = cls.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Excel ex = declaredField.getAnnotation(Excel.class);
            if(ex == null){
                continue;
            }
            String name = ex.name();
            headers.add(name);
        }

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setPadding(8);
            cell.setBorderWidth(1.5f);
            table.addCell(cell);
        }
    }

    private  void addUserTableRows(PdfPTable table) throws IllegalAccessException {
        for (int i = 0; i < dataList.size(); i++) {
            T user = dataList.get(i);

            // 交替行背景色
//            if (i % 2 == 0) {
//                table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
//            } else {
//                table.getDefaultCell().setBackgroundColor(ROW_BG_COLOR);
//            }

            Field[] declaredFields = cls.getDeclaredFields();


            for (Field declaredField : declaredFields) {
                Excel ex = declaredField.getAnnotation(Excel.class);
                if(ex == null){
                    continue;
                }



                Object fieldValue = BeanUtil.getFieldValue(user,declaredField.getName()) ;
                String value = fieldValue == null ? "": fieldValue.toString();

                table.addCell(createCell(value));
            }
        }
    }

    private  PdfPCell createCell(String content) {
        return createCell(content, Element.ALIGN_LEFT);
    }

    private  PdfPCell createCell(String content, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, CELL_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderWidth(1f);
        return cell;
    }
}
