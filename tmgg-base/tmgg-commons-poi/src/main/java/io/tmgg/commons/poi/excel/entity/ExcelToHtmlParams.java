package io.tmgg.commons.poi.excel.entity;

import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel到HTML的参数
 * @author JueYue
 *  2015年8月7日 下午1:45:46
 */
@Data
public class ExcelToHtmlParams {
    /**
     * Excel
     */
    private Workbook wb;
    /**
     * 是不是全界面
     */
    private boolean  completeHTML = false;
    /**
     * 位置
     */
    private int      sheetNum = 0;

    /**
     *  -- 图片保存路径,/开始或者含有: 认为是绝对路径,其他是相对路径,每次img名称随机生成,按照天生成文件夹
     *  -- 不生成图片了直接BASE64Encoder
     * 不为空就认为显示图片
     */
    private String   path = null;

    private boolean  showRowNum = false;
    private boolean  showColumnHead = false;

    public ExcelToHtmlParams(Workbook wb) {
        this.wb = wb;
    }

    public ExcelToHtmlParams(Workbook wb, boolean completeHTML) {
        this.wb = wb;
        this.completeHTML = completeHTML;
    }

    public ExcelToHtmlParams(Workbook wb, int sheetNum) {
        this.wb = wb;
        this.sheetNum = sheetNum;
    }

    public ExcelToHtmlParams(Workbook wb, String path) {
        this.wb = wb;
        this.path = path;
    }

    public ExcelToHtmlParams(Workbook wb, int sheetNum, String path) {
        this.wb = wb;
        this.sheetNum = sheetNum;
        this.path = path;
    }

    public ExcelToHtmlParams(Workbook wb, boolean completeHTML,  String path) {
        this.wb = wb;
        this.completeHTML = completeHTML;
        this.path = path;
    }

    public ExcelToHtmlParams(Workbook wb, boolean completeHTML, int sheetNum, String path) {
        this.wb = wb;
        this.completeHTML = completeHTML;
        this.sheetNum = sheetNum;
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExcelToHtmlParams) {
            ExcelToHtmlParams other = (ExcelToHtmlParams) obj;
            if (!this.wb.equals(other.getWb()) || this.completeHTML != other.completeHTML
                || this.sheetNum != other.getSheetNum()) {
                return false;
            }
            if ((this.path == null && other.getPath() != null)
                || !this.path.equals(other.getPath())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 保持一个参数一个对象的hashCode
     */
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(wb.hashCode());
        sb.append(path);
        sb.append(completeHTML);
        sb.append(sheetNum);
        return sb.toString().hashCode();
    }

}
