package io.github.tmgg;

import io.tmgg.jackson.JsonTool;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetReadme {


    public static final File README = new File("README.md");
    List<String> versionLines = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        SetReadme sv = new SetReadme();
        sv.changeMaven();
        sv.changeNpm();


        List<String> lines = FileUtils.readLines(README, StandardCharsets.UTF_8);


        int begin = lines.indexOf("# 模块");
        int end = lines.indexOf("# 文档");

        List<String> newLines = new ArrayList<>();
        for (int i = 0; i < lines.size() ; i++) {
            String e = lines.get(i);

            if(i == begin){
                newLines.add(e);
                newLines.addAll(sv.versionLines);
                continue;
            }

            if( i > begin && i < end){
                continue;
            }

            newLines.add(e);
        }



        FileUtils.writeLines(README, newLines);


    }


    private void changeMaven() throws IOException {
        versionLines.add("## Maven 包");
        Collection<File> poms = DevFileUtil.findByPrefix(".", "pom.xml", "tmgg-" );


        for (File pom : poms) {
            String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(xml, Parser.xmlParser());

            String artifactId = doc.selectFirst("project>artifactId").text();
            Element description = doc.selectFirst("project>description");
            versionLines.add(" ");
            String mvn = "![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tmgg/" + artifactId + ")";
            versionLines.add(mvn + artifactId);
            if(description != null){
                versionLines.add(description.text().trim());
            }
        }

    }

    private void changeNpm() throws IOException {
        versionLines.add("## NPM 包");

        File file = new File("web-monorepo");

        List<String> lines = FileUtils.readLines(new File(file, "pnpm-workspace.yaml"));

        lines.remove(0);
        lines = lines.stream().map(String::trim).map(line -> line.substring(1, line.length() -2).trim()).toList();


        Collection<File> pkgs = DevFileUtil.find(file.getPath(), "package.json", lines);


        for (File pkg : pkgs) {
            String json = FileUtils.readFileToString(pkg, StandardCharsets.UTF_8);

            Map<String, Object> map = JsonTool.jsonToMap(json);

            Object name = map.get("name");
            if("true".equals(map.get("private"))){
                continue;
            }

            versionLines.add(" ");
            String npm = " ![NPM Version](https://img.shields.io/npm/v/" + name + ")";
            versionLines.add(npm + name);
            Object description = map.get("description");
            if(description != null){
                versionLines.add(description.toString());
            }
        }
    }



}
