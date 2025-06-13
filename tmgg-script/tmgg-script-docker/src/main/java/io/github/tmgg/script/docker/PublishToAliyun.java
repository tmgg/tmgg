package io.github.tmgg.script.docker;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.ResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.common.collect.Sets;
import io.tmgg.lang.FileTool;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * 构建基础镜像，推送阿里云
 */
@Slf4j
public class PublishToAliyun {

    private String url = "registry.cn-hangzhou.aliyuncs.com";
    private String namespace = "mxvc";
    private String username = "hustme";
    private String password = "password";
    File root;
    private String version;

    public static void main(String[] args) throws IOException, InterruptedException {
        Assert.state(args.length == 2);

        PublishToAliyun aliyun = new PublishToAliyun();
        aliyun.password = args[0];
        aliyun.version = args[1];

        aliyun.process();
    }

    public PublishToAliyun() {
        root = new File(".");
        root = new File(root.getAbsolutePath());

        if (root.getAbsolutePath().contains("tmgg-script-docker")) {
            root = FileTool.findParentByName(root, "tmgg-script").getParentFile();
        }
        log.info("根目录 {}", root.getAbsolutePath());

    }

    private void process() throws InterruptedException {
        File dockerfiles = new File(root, "dockerfiles");
        for (File dir : dockerfiles.listFiles()) {
            log.info(dir.getAbsolutePath());
            buildAndPush(dir);
        }
    }

    private void buildAndPush(File dir) throws InterruptedException {
        String tag1 = "%s/%s/%s:%s".formatted(url, namespace, dir.getName(), "latest");
        String tag2 = "%s/%s/%s:%s".formatted(url, namespace, dir.getName(), version);
        log.info("tag1: {}", tag1);
        log.info("tag2: {}", tag2);
        Set<String> tags = Sets.newHashSet(tag1, tag2);

        DockerClient client = getClient();


        String imageId = client.buildImageCmd()
                .withTags(tags)
                .withForcerm(true)
                .withBaseDirectory(root)
                .withBuildArg("VERSION", version)
                .withDockerfile(new File(dir, "Dockerfile"))
                .exec(new BuildImageResultCallback() {
                    @Override
                    public void onNext(BuildResponseItem item) {
                        super.onNext(item);
                        print(item);
                    }
                }).awaitImageId();

        log.info("构建完成,imageId {}", imageId);

        for (String tag : tags) {
            client.pushImageCmd(tag).exec(new PushImageResultCallback() {
                @Override
                public void onNext(PushResponseItem item) {
                    super.onNext(item);
                    print(item);
                }
            }).awaitCompletion();
            log.info("推送镜像结束");

        }


        log.info("阶段结束");
    }

    private void print(ResponseItem item) {
        String stream = item.getStream();
        if (StrUtil.isNotEmpty(stream)) {
            System.out.println(stream);
        }
    }


    public String getLocalDockerHost() {
        boolean windows = SystemUtil.getOsInfo().isWindows();
        log.info("windows " + windows);
        return windows ? "tcp://localhost:2375" : "unix:///var/run/docker.sock";
    }

    public DockerClient getClient() {
        String dockerHost = getLocalDockerHost();
        log.info("docker host: " + dockerHost);

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

