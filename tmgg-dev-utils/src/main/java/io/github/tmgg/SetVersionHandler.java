package io.github.tmgg;

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

public class SetVersionHandler {

    public static final String NEW_VERSION = "0.1.14";


    public static void main(String[] args) throws IOException {
        SetVersionHandler sv = new SetVersionHandler(NEW_VERSION);
        sv.changeMaven();
        sv.changeNpm();
    }

    private void changeNpm() {
    }

    private void changeMaven() throws IOException {
        File file = new File(".");
        Collection<File> poms = FileUtils.listFiles(file, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.equals("pom.xml");
            }

            @Override
            public boolean accept(File file, String s) {
                return false;
            }
        }, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                String baseName = FilenameUtils.getBaseName(file.getName());
                return baseName.startsWith("tmgg");
            }

            @Override
            public boolean accept(File file, String s) {
                return false;
            }
        });

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


    public SetVersionHandler(String version) {
        this.version = version;
    }

    private String version;


}
