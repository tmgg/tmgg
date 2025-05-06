package io.tmgg.report.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 简单示例
 */
@RequestMapping("ureport")
@RestController
public class UReportController {


    @HasPermission
    @PostMapping("page")
    public AjaxResult page() {
        Collection<ReportProvider> list = SpringTool.getBeans(ReportProvider.class);

        list = list.stream().filter(t -> !t.disabled()).toList();


        List<Dict> voList = new ArrayList<>();
        for (ReportProvider reportProvider : list) {
            List<ReportFile> reportFiles = reportProvider.getReportFiles();
            if(CollUtil.isEmpty(reportFiles)){
                continue;
            }
            for (ReportFile file : reportFiles) {
                Dict dict = new Dict();
                dict.put("providerName", reportProvider.getName());
                dict.put("providerPrefix",reportProvider.getPrefix());
                dict.put("name", file.getName());
                dict.put("updateDate", file.getUpdateDate());
                dict.put("previewUrl", "ureport/preview?_u=" + reportProvider.getPrefix() + file.getName());
                dict.put("designerUrl", "ureport/designer?_u=" + reportProvider.getPrefix() + file.getName());
                voList.add(dict);
            }
        }

        return AjaxResult.ok().data(new PageImpl<>(voList));
    }


}
