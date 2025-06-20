package io.tmgg.modules.system.file;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class MinioFileOperator implements FileOperator {


    private String url;
    private String accessKey;
    private String secretKey;

    private String bucketName;

    public MinioFileOperator(String url, String accessKey, String secretKey, String bucketName) {
        this.url = url;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;

        this.initClient();
    }

    private MinioClient client;

    public void initClient() {
        this.client = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
    }

    @Override
    public void save(String key, InputStream inputStream) throws Exception {
        PutObjectArgs arg = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .stream(inputStream, inputStream.available(), -1)
                .build();

       this.client.putObject(arg);

    }

    @Override
    public InputStream getFileStream( String key) throws Exception {

        GetObjectResponse response = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(key).build());
        return response;
    }



    @Override
    public void delete(String key) throws Exception {
        client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(key).build());
    }

}
