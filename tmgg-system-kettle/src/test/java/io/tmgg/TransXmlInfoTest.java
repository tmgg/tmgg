package io.tmgg;

import io.tmgg.kettle.controller.TransXmlInfo;
import io.tmgg.jackson.JsonTool;
import io.tmgg.jackson.XmlTool;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TransXmlInfoTest {
    public static void main(String[] args) throws IOException {
        String xml = FileUtils.readFileToString(new File("D:\\1.xml"), StandardCharsets.UTF_8);
        System.out.println(xml);

        TransXmlInfo info = XmlTool.xmlToBean(xml, TransXmlInfo.class);

        System.out.println(JsonTool.toPrettyJsonQuietly(info));
    }
}
