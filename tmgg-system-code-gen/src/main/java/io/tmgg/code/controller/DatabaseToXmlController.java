package io.tmgg.code.controller;


import io.tmgg.lang.SpringTool;
import io.tmgg.lang.XmlTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.web.token.TokenManger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("code/db2xml")
public class DatabaseToXmlController {

    @Resource
    TokenManger tokenManger;

    @GetMapping("index")
    public void index(HttpServletResponse response, HttpServletRequest req) throws IOException {

        Map<String, BaseDao> beans = SpringTool.getBeansOfType(BaseDao.class);

        response.setContentType("text/html;charset=utf8");
        PrintWriter writer = response.getWriter();


        writer.append("<h1>数据访问层列表</h1>");
        writer.append("<p>提示：可直接放到项目资源目录下，则系统启动时自动加载。如：src/main/resources/database/xxx.xml </p>");

        List<String> names = new ArrayList<>(beans.keySet());
        Collections.sort(names);
        for (String beanName : names) {

            String tokenKey = TokenManger.URL_PARAM;
            String token = tokenManger.getTokenFromRequest(req);

            String url = "export?beanName=" + beanName + "&" + tokenKey + "=" + token;
            String div = String.format("<div>%s &nbsp;&nbsp; <a href='%s'>导出</a></div>", beanName, url);
            writer.append(div);
        }

    }

    @GetMapping("export")
    public void export(String beanName, HttpServletResponse response) throws IOException {
        BaseDao dao = SpringTool.getBean(beanName);
        List all = dao.findAll();


       String filename = "data_" +beanName.replace("Dao","") +".xml";
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.setHeader("Access-Control-Expose-Headers", "content-disposition");
        response.setCharacterEncoding("UTF-8");


        PrintWriter writer = response.getWriter();

        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                     "<root>");
        for (Object o : all) {
            String s = XmlTool.beanToXml(o);
            writer.write(s);
        }
        writer.write("</root>");
    }



}
