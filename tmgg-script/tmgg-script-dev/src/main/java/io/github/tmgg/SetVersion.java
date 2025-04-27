package io.github.tmgg;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
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

    public static final String NEW_VERSION = "0.0.1";


    public static void main(String[] args) throws IOException {
        SetVersion sv = new SetVersion();
        sv.changeNpm();
    }


    private void changeNpm() throws IOException {
        File file = new File("web-monorepo");

        List<String> lines = FileUtils.readLines(new File(file, "pnpm-workspace.yaml"));

        lines.remove(0);
        lines = lines.stream().map(String::trim).map(line -> line.substring(1, line.length() - 2).trim()).toList();

        System.out.println(lines);


        List<File> pkgs = DevFileUtil.find(file.getPath(), "package.json", lines);


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
