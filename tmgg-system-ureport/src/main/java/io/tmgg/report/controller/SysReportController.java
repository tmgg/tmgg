package io.tmgg.report.controller;

import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.token.TokenManger;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 简单示例
 */
@RequestMapping("sysReport")
@RestController
public class SysReportController {

    @Resource
    TokenManger tokenManger;

    @GetMapping("list")
    public AjaxResult list(HttpServletRequest request) {
        String token = tokenManger.getTokenFromRequest(request);

        Collection<ReportProvider> list = SpringTool.getBeans(ReportProvider.class);

        List<ReportVo> voList = new ArrayList<>();
        for (ReportProvider provider : list) {
            List<ReportFile> reportFiles = provider.getReportFiles();
            for (ReportFile reportFile : reportFiles) {
                String name = reportFile.getName();

                String url = "ureport/preview?_u=" + provider.getPrefix() +  name + "&"+TokenManger.URL_PARAM+"=" + token;
                voList.add(new ReportVo(name, url));
            }
        }

        return AjaxResult.success(voList);
    }

    @Data
    @AllArgsConstructor
    public static class ReportVo {
        String baseName;
        String url;

    }
}