package io.github.tmgg.script.docker;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.tmgg.lang.FileTool;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 构建基础镜像，推送阿里云
 */
@Slf4j
public class PublishToAliyun {

    private static String url = "registry.cn-hangzhou.aliyuncs.com";
    private static String namespace = "mxvc";
    private static String username = "hustme";
    private static String password = "password";


    public static void main(String[] args) throws IOException, InterruptedException {
        Assert.state(args.length == 1);
        password = args[1];


        log.info("部署密码为 {}", password);

        File root = new File(".");
        root = new File(root.getAbsolutePath());

        if (root.getAbsolutePath().contains("tmgg-script-docker")) {
            root = FileTool.findParentByName(root, "tmgg-script").getParentFile();
        }
        log.info("根目录 {}", root.getAbsolutePath());


        buildAndPush(root, "java", new File(root, "template-backend"));

        buildAndPush(root, "node", new File(root, "web-monorepo/template/web"));

    }

    private static void buildAndPush(File root, String type, File dir) throws InterruptedException {

        File dockerfile = new File(root, "dockerfiles/base-" + type + "-image/Dockerfile");
        log.info("Dockerfile路径 {}", dockerfile.getAbsolutePath());
        log.info("是否存在 {}", dockerfile.exists());
        String tag = "%s/%s/tmgg-base-%s:latest".formatted(url, namespace, type);

        DockerClient client = getClient();


        String imageId = client.buildImageCmd(dockerfile).withTag(tag)
                .withForcerm(true)
                .withBaseDirectory(dir)
                .withDockerfile(dockerfile)
                .exec(new BuildImageResultCallback() {
                    @Override
                    public void onNext(BuildResponseItem item) {
                        super.onNext(item);
                        String stream = item.getStream();
                        if (StrUtil.isNotEmpty(stream)) {
                            System.out.println(stream);
                        }
                    }
                }).awaitImageId();

        log.info("构建完成,imageId {}", imageId);
        client.pushImageCmd(tag).exec(new PushImageResultCallback() {
            @Override
            public void onNext(PushResponseItem item) {
                super.onNext(item);
                String stream = item.getStream();
                if (StrUtil.isNotEmpty(stream)) {
                    System.out.println(stream);
                }
            }
        }).awaitCompletion();
        log.info("推送镜像结束");

        log.info("阶段结束");
    }


    public static String getLocalDockerHost() {
        boolean windows = SystemUtil.getOsInfo().isWindows();
        System.out.println("windows " + windows);
        return windows ? "tcp://localhost:2375" : "unix:///var/run/docker.sock";
    }

    public static DockerClient getClient() {
        String dockerHost = getLocalDockerHost();
        System.out.println("docker host: " + dockerHost);

        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withRegistryUrl(url)
                .withRegistryUsername(username)
                .withRegistryPassword(password);

        DockerClientConfig config = builder.build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost()).sslConfig(config.getSSLConfig()).build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        return dockerClient;
    }

}
