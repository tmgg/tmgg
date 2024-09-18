package io.github.tmgg;

import cn.moon.lang.json.JsonTool;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetVersionHandler {

    public static final String NEW_VERSION = "0.1.23";


    public static void main(String[] args) throws IOException {
        SetVersionHandler sv = new SetVersionHandler(NEW_VERSION);
        sv.changeMaven();
        sv.changeNpm();
    }


    private void changeMaven() throws IOException {
        Collection<File> poms = FileUtil.findByPrefix(".", "pom.xml", "tmgg-" );

        System.out.println("开始修改pom.xml的版本");

        for (File pom : poms) {
            System.out.println(pom);
            String xml = FileUtils.readFileToString(pom, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(xml, Parser.xmlParser());

            boolean parent = pom.getPath().equals("pom.xml");
            if (parent) {
                doc.selectFirst("project>version").text(version);
                doc.selectFirst("tmgg\\.version").text(version);
            } else {
                doc.selectFirst("parent>version").text(version);
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


        Collection<File> pkgs = FileUtil.find(file.getPath(), "package.json", lines);


        for (File pkg : pkgs) {
            System.out.println(pkg);
            String json = FileUtils.readFileToString(pkg, StandardCharsets.UTF_8);

            Map<String, Object> map = JsonTool.jsonToMap(json);
            map.put("version", version);

            json = JsonTool.toPrettyJsonQuietly(map);

            FileUtils.writeStringToFile(pkg, json, StandardCharsets.UTF_8);
        }
    }

    private SetVersionHandler(String version) {
        this.version = version;
    }

    private String version;


}
