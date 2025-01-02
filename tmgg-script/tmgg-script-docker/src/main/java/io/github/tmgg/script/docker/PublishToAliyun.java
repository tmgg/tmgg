package io.github.tmgg.script.docker;


import cn.hutool.system.SystemUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
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

public class PublishToAliyun {
    static String url = "registry.cn-hangzhou.aliyuncs.com";
    static String namespace = "tmgg";
    static String username = "hustme";

    static String password = "xxx";

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            password = args[0];
        }
        System.out.println("部署密码为：" + password);


        DockerClient client = getClient();

        File root = new File(".");
        System.out.println("构建目录为:" + root.getAbsolutePath());

        String projectVersion = getProjectVersion();
        System.out.println("项目版本为：" + projectVersion);

        File dockerfile = new File(root, "tmgg-script/tmgg-script-docker/dockerfile/base-java-image/Dockerfile");

        String image1 = url + "/" + namespace + "/tmgg-base-java-image:" + projectVersion;
        String image2 = url + "/" + namespace + "/tmgg-base-java-image:latest";
        Set<String> tags = new HashSet<>();
        tags.add(image1);
        tags.add(image2);

        BuildImageResultCallback resultCallback = new BuildImageResultCallback();
        String imageId = client.buildImageCmd(dockerfile).withTags(tags)
                .withForcerm(true)
                .withBaseDirectory(new File(root, "doc/project-template"))
                .exec(resultCallback).awaitImageId();

        System.out.println("构建完成,imageId: " + imageId);

    }

    private static String getProjectVersion() throws IOException {
        File pom = new File(".", "pom.xml");
        String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(xml, Parser.xmlParser());

        Element el = doc.selectFirst("project>version");


        return el.text();

    }

    public static String getLocalDockerHost() {
        boolean windows = SystemUtil.getOsInfo().isWindows();
        return windows ? "tcp://localhost:2375" : "unix:///var/run/docker.sock";
    }

    public static DockerClient getClient() {
        String dockerHost = getLocalDockerHost();


        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withRegistryUrl(url)
                .withRegistryUsername(username)
                .withRegistryPassword(password);


        DockerClientConfig config = builder.build();

        DockerHttpClient httpClient = new  ApacheDockerHttpClient.Builder().dockerHost(config.getDockerHost()).sslConfig(config.getSSLConfig()).build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config,httpClient);


        return dockerClient;
    }

}
