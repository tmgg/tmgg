package io.github.tmgg;

import cn.hutool.core.io.FileUtil;
import io.tmgg.Build;
import io.tmgg.jackson.JsonTool;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetVersion {

    public static final String NEW_VERSION = "0.3.93";
    public static final String APPLICATION_TEMPLATE = "tmgg-application-template";


    public static void main(String[] args) throws IOException {
        SetVersion sv = new SetVersion();
        sv.changeMaven();
        sv.changeNpm();
        sv.changeJavaBuildVersion();
    }

    private void changeJavaBuildVersion() {
        String javaFile = "tmgg-base\\tmgg-commons-lang\\src\\main\\java\\io\\tmgg\\Build.java";
        File file = new File(javaFile);

        System.out.println(file.exists());

        List<String> lines = FileUtil.readUtf8Lines(file);

        System.out.println("Build.java的当前版本为：" + Build.FRAMEWORK_VERSION);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.contains("FRAMEWORK_VERSION")) {
                System.out.println();
                line = "public static String FRAMEWORK_VERSION = \"" + NEW_VERSION + "\";";
                lines.set(i, line);
                System.out.println(line);
            }
        }


        FileUtil.writeUtf8Lines(lines, file);


        System.out.println();

    }


    private void changeMaven() throws IOException {
        Collection<File> poms = DevFileUtil.findByPrefix(".", "pom.xml", "tmgg-");
        poms.add(new File(APPLICATION_TEMPLATE + "/pom.xml"));


        for (File pom : poms) {
            System.out.println(pom);
            String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(xml, Parser.xmlParser());

            boolean parent = pom.getPath().equals("pom.xml");
            if (parent) {
                doc.selectFirst("project>version").text(NEW_VERSION);
                doc.selectFirst("tmgg\\.version").text(NEW_VERSION);
            } else {
                doc.selectFirst("parent>version").text(NEW_VERSION);
            }

            xml = doc.toString();

            FileUtils.writeStringToFile(pom, xml, StandardCharsets.UTF_8);
        }

    }

    private void changeNpm() throws IOException {
        File file = new File("web-monorepo");

        List<String> lines = FileUtils.readLines(new File(file, "pnpm-workspace.yaml"));

        lines.remove(0);
        lines = lines.stream().map(String::trim).map(line -> line.substring(1, line.length() - 2).trim()).toList();

        System.out.println(lines);


        List<File> pkgs = DevFileUtil.find(file.getPath(), "package.json", lines);
        pkgs.add(0,new File(APPLICATION_TEMPLATE+"/web/package.json"));


        for (File pkg : pkgs) {

            System.out.println(pkg);
            String json = FileUtils.readFileToString(pkg, StandardCharsets.UTF_8);

            Map<String, Object> map = JsonTool.jsonToMap(json);
            map.put("version", NEW_VERSION);

            replaceProjectTemplate(pkg, map);

            json = JsonTool.toPrettyJsonQuietly(map);

            FileUtils.writeStringToFile(pkg, json, StandardCharsets.UTF_8);
        }
    }

    private static void replaceProjectTemplate(File pkg, Map<String, Object> map) {
        String path = pkg.getPath();
        System.out.println(path);
        System.out.println(APPLICATION_TEMPLATE);
        if (path.contains(APPLICATION_TEMPLATE)) {
            Map<String, Object> dependencies = (Map<String, Object>) map.get("dependencies");
            for (String key : dependencies.keySet()) {
                if (key.startsWith("@tmgg/tmgg-")) {
                    dependencies.put(key, NEW_VERSION);
                }
            }
            System.out.println("处理模板中...");
        }
    }


}
