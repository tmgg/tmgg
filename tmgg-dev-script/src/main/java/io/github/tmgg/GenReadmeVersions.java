package io.github.tmgg;

import cn.moon.lang.json.JsonTool;
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

public class GenReadmeVersions {


    StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {
        GenReadmeVersions sv = new GenReadmeVersions();
        sv.changeMaven();
        sv.changeNpm();

        System.out.println(sv.sb);
    }


    private void changeMaven() throws IOException {
        sb.append("## Maven 包\r\n");
        Collection<File> poms = FileUtil.findByPrefix(".", "pom.xml", "tmgg-" );


        for (File pom : poms) {
            String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(xml, Parser.xmlParser());

            String artifactId = doc.selectFirst("project>artifactId").text();

            sb.append("![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tmgg/"+artifactId+"?label="+artifactId+") ");
            sb.append("\r\n");
        }

    }

    private void changeNpm() throws IOException {
        sb.append("## NPM 包\r\n");

        File file = new File("web-monorepo");

        List<String> lines = FileUtils.readLines(new File(file, "pnpm-workspace.yaml"));

        lines.remove(0);
        lines = lines.stream().map(String::trim).map(line -> line.substring(1, line.length() -2).trim()).toList();


        Collection<File> pkgs = FileUtil.find(file.getPath(), "package.json", lines);


        for (File pkg : pkgs) {
            String json = FileUtils.readFileToString(pkg, StandardCharsets.UTF_8);

            Map<String, Object> map = JsonTool.jsonToMap(json);

            Object name = map.get("name");
            sb.append("![NPM Version](https://img.shields.io/npm/v/"+name+"?label="+name+")");
            sb.append("\r\n");
        }
    }



}
