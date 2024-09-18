package io.github.tmgg;

import cn.moon.lang.json.JsonTool;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ModifyReadmeVersions {


    List<String> versionLines = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ModifyReadmeVersions sv = new ModifyReadmeVersions();
        sv.changeMaven();
        sv.changeNpm();


        List<String> lines = FileUtils.readLines(new File("README.md"), StandardCharsets.UTF_8);

        List<String> newLines = new ArrayList<>();

        boolean inVersionArea = false;
        for (String line : lines) {
            if(inVersionArea){
                continue;
            }

            newLines.add(line);

            if(line.equals("# 版本")){
                newLines.addAll(sv.versionLines);
                inVersionArea = true;
                continue;
            }

            if(line.equals("# 文档")){
                inVersionArea = false;
            }

        }




    }


    private void changeMaven() throws IOException {
        versionLines.add("## Maven 包");
        Collection<File> poms = DevFileUtil.findByPrefix(".", "pom.xml", "tmgg-" );


        for (File pom : poms) {
            String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(xml, Parser.xmlParser());

            String artifactId = doc.selectFirst("project>artifactId").text();

            versionLines.add("![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tmgg/"+artifactId+"?label="+artifactId+") ");
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
            versionLines.add("![NPM Version](https://img.shields.io/npm/v/"+name+"?label="+name+")");
        }
    }



}
