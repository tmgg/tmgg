
package io.tmgg.sys.file;

import lombok.Data;

/**
 * 本地文件存储配置
 *
 */
@Data
public class LocalFileProperties {

    /**
     * 本地文件存储位置（linux）
     */
    private String localFileSavePathLinux = "/tmp/tempFilePath";

    /**
     * 本地文件存储位置（windows）
     */
    private String localFileSavePathWin = "D:\\tempFilePath";

}
