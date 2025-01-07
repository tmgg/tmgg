package io.tmgg.modules.sys.file;

import cn.hutool.core.util.StrUtil;
import io.minio.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MinioFileOperator implements FileOperator {


    private String url;
    private String accessKey;
    private String secretKey;

    private String defaultBucketName;

    public MinioFileOperator(String url, String accessKey, String secretKey, String defaultBucketName) {
        this.url = url;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.defaultBucketName = defaultBucketName;

        this.initClient();
    }

    private MinioClient client;

    public void initClient() {
        this.client = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
    }

    @Override
    public void storageFile(String bucketName, String key, byte[] bytes) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, defaultBucketName);

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        PutObjectArgs arg = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .stream(is, bytes.length, -1)
                .build();


        this.client.putObject(arg);
        is.close();
    }

    @Override
    public void storageFile(String bucketName, String key, InputStream inputStream) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, defaultBucketName);

        PutObjectArgs arg = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .stream(inputStream, inputStream.available(), -1)
                .build();

        this.client.putObject(arg);
    }

    @Override
    public byte[] getFileBytes(String bucketName, String key) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, defaultBucketName);

        GetObjectResponse response = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(key).build());
        byte[] bytes = IOUtils.toByteArray(response);
        response.close();

        return bytes;
    }

    @Override
    public InputStream getFileStream(String bucketName, String key) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, defaultBucketName);

        GetObjectResponse response = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(key).build());
        return response;
    }



    @Override
    public void deleteFile(String bucketName, String key) throws Exception {
        bucketName = StrUtil.emptyToDefault(bucketName, defaultBucketName);

        client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(key).build());
    }


    public static void main(String[] args) throws Exception {
        MinioFileOperator operator = new MinioFileOperator("https://minio.ztstc.cn/", "sqky", "Us#k!09J8d", "sqky");

       operator.storageFile(null,"test56.txt", "hello world".getBytes());
        System.out.println("上传结束");

    }

}
