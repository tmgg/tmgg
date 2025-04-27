package io.tmgg;

import io.tmgg.kettle.controller.JobXmlInfo;
import io.tmgg.jackson.JsonTool;
import io.tmgg.jackson.XmlTool;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JobXmlInfoTest {
    public static void main(String[] args) throws IOException {
        String xml = FileUtils.readFileToString(new File("D:\\job22.kjb"), StandardCharsets.UTF_8);
        System.out.println(xml);

        JobXmlInfo info = XmlTool.xmlToBean(xml, JobXmlInfo.class);

        System.out.println(JsonTool.toPrettyJsonQuietly(info));
    }
}
