package io.github.tmgg.script.docker;


import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.PullImageResultCallback;
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
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 构建基础镜像，推送阿里云
 */
@Slf4j
public class PublishToAliyun {
    public static final String APPLICATION_TEMPLATE = "tmgg-application-template";
    private static String url = "registry.cn-hangzhou.aliyuncs.com";
    private static String namespace = "mxvc";
    private static String username = "hustme";
    private static String password = "password";

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            password = args[0];
        }
        log.info("部署密码为 {}", password);

        File root = new File(".");
        log.info("根目录 {}", root.getAbsolutePath());
        root = new File(root.getAbsolutePath());

        if (root.getAbsolutePath().contains("tmgg-script-docker")) {
            root = FileTool.findParentByName(root, "tmgg-script").getParentFile();
        }
        log.info("根目录 {}", root.getAbsolutePath());

        String projectVersion = getProjectVersion(root);
        log.info("项目版本为 {}", projectVersion);

        buildAndPush(projectVersion, root, "node");
        buildAndPush(projectVersion, root, "jdk");
        buildAndPush(projectVersion, root, "java");

    }

    private static void buildAndPush(String projectVersion, File root, String type) throws InterruptedException {
        String image1 = url + "/" + namespace + "/tmgg-base-" + type + ":" + projectVersion;
        String image2 = url + "/" + namespace + "/tmgg-base-" + type + ":latest";
        Set<String> tags = new HashSet<>();
        tags.add(image1);
        tags.add(image2);

        File templateProject = new File(root, APPLICATION_TEMPLATE);
        File dockerfile = new File(templateProject, "assets/dockerfiles/base-" + type + "-image/Dockerfile");
        log.info("Dockerfile路径 {}", dockerfile.getAbsolutePath());
        log.info("是否存在 {}", dockerfile.exists());

        DockerClient client = getClient();


        String imageId = client.buildImageCmd(dockerfile).withTags(tags)
                .withForcerm(true)
                .withBaseDirectory(templateProject)
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
        for (String tag : tags) {
            log.info("推送镜像:" + tag);
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
        }

        log.info("阶段结束");
    }

    private static String getProjectVersion(File root) throws IOException {
        File pom = new File(root, "pom.xml");
        String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(xml, Parser.xmlParser());

        Element el = doc.selectFirst("project>version");

        return el.text();
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
