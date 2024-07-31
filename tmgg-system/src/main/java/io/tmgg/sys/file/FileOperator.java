
package io.tmgg.sys.file;


import java.io.File;
import java.io.InputStream;

/**
 * 文件操纵者（内网操作）
 * 如果存在未包含的操作，可以调用getClient()自行获取client进行操作
 *
 */
public interface FileOperator {



    /**
     * 存储文件
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param bytes      文件字节数组
     */
    void storageFile(String bucketName, String key, byte[] bytes) throws Exception;

    /**
     * 存储文件 ,流式
     *
     * @param bucketName  桶名称
     * @param key         唯一标示id，例如a.txt, doc/a.txt
     * @param inputStream 文件流
     */
    void storageFile(String bucketName, String key, InputStream inputStream) throws Exception;

    /**
     * 获取某个bucket下的文件字节
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     */
    byte[] getFileBytes(String bucketName, String key) throws Exception;

    InputStream getFileStream(String bucketName, String key) throws Exception;

    /**
     * 删除文件
     *
     * @param bucketName 文件桶
     * @param key        文件唯一标识
     */
    void deleteFile(String bucketName, String key) throws Exception;


}
