package io.tmgg.lang;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;

@Slf4j
public class FreemarkerTool {


    public static String renderString(String templateString, Object model) throws Exception {
        StringWriter result = new StringWriter();
        Template t = new Template("name", templateString, new Configuration(Configuration.VERSION_2_3_23));
        t.process(model, result);
        return result.toString();
    }


}
