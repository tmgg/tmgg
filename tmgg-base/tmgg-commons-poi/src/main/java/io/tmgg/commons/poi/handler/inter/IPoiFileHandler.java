package io.tmgg.commons.poi.handler.inter;

import org.apache.commons.lang3.NotImplementedException;

/**
 * 文件保存服务，支持客户自定义文件的保存
 */
public interface IPoiFileHandler {

    public final static String IMAGE = "img";
    public final static String EXCEL = "excel";


    default public String saveImgFile(String fileName, byte[] data, Object object) {
        throw new NotImplementedException("未实现，请勿使用");
    }
}
