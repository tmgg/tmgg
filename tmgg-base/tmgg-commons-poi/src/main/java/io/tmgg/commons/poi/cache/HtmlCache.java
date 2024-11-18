package io.tmgg.commons.poi.cache;

import io.tmgg.commons.poi.excel.entity.ExcelToHtmlParams;
import io.tmgg.commons.poi.excel.html.ExcelToHtmlService;

/**
 * Excel 转变成为Html 的缓存
 *
 * @author JueYue
 *         2015年8月7日 下午1:29:47
 */
public class HtmlCache {

    public static String getHtml(ExcelToHtmlParams params) {
        try {
            return new ExcelToHtmlService(params).printPage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
