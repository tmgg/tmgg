package io.tmgg.kettle.controller;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.ResultData;
import io.github.tmgg.kettle.sdk.response.SlaveServerJobStatus;
import io.github.tmgg.kettle.sdk.response.SlaveServerStatus;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("kettle")
public class KettleController {

    @Resource
    KettleSdk sdk;


    @RequestMapping("status")
    public AjaxResult status() {
        try {
            SlaveServerStatus status = sdk.status();
            return AjaxResult.ok().data(status);
        } catch (Exception e) {
            return AjaxResult.err().msg("获取信息失败");
        }
    }

    @RequestMapping("jobStatus")
    public AjaxResult jobStatus(String jobId, String jobName) {
        try {
            SlaveServerJobStatus status = sdk.jobStatus(jobId, jobName);
            return AjaxResult.ok().data(status);
        } catch (Exception e) {
            return AjaxResult.err().msg("获取作业状态失败");
        }
    }

    @GetMapping("jobImage")
    public AjaxResult jobImage(String jobId, String jobName) throws IOException {
        ResultData<byte[]> rs = sdk.jobImage(jobId, jobName);

        if (rs.isSuccess()) {
            return AjaxResult.ok().data(rs.getData());
        }

        return AjaxResult.err().msg(rs.getMessage());
    }

    @GetMapping("xml")
    public AjaxResult xml(String id) throws IOException {
        try {
            String xml = sdk.getRepositoryObjectContent(id);
            return AjaxResult.ok().data(xml);
        } catch (Exception e) {
            return AjaxResult.err().msg(e.getMessage());
        }
    }
}
