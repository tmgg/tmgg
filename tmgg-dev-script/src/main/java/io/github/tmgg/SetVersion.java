package io.github.tmgg;

import cn.moon.lang.json.JsonTool;
import lombok.extern.slf4j.Slf4j;
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

    public static final String NEW_VERSION = "0.1.88";


    public static void main(String[] args) throws IOException {
        SetVersion sv = new SetVersion();
        sv.changeMaven();
        sv.changeNpm();
    }


    private void changeMaven() throws IOException {
        Collection<File> poms = DevFileUtil.findByPrefix(".", "pom.xml", "tmgg-" );

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
        lines = lines.stream().map(String::trim).map(line -> line.substring(1, line.length() -2).trim()).toList();

        System.out.println(lines);


        Collection<File> pkgs = DevFileUtil.find(file.getPath(), "package.json", lines);


        for (File pkg : pkgs) {
            System.out.println(pkg);
            String json = FileUtils.readFileToString(pkg, StandardCharsets.UTF_8);

            Map<String, Object> map = JsonTool.jsonToMap(json);
            map.put("version", NEW_VERSION);

            json = JsonTool.toPrettyJsonQuietly(map);

            FileUtils.writeStringToFile(pkg, json, StandardCharsets.UTF_8);
        }
    }



}
