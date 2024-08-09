package io.tmgg.kettle.controller;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.ResultData;
import io.github.tmgg.kettle.sdk.response.SlaveServerJobStatus;
import io.github.tmgg.kettle.sdk.response.SlaveServerStatus;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("kettle")
public class KettleController {

    @Resource
    KettleSdk sdk;



    @RequestMapping("status")
    public AjaxResult status() {
        SlaveServerStatus status = sdk.status();

        return AjaxResult.ok().data(status);
    }

    @RequestMapping("jobStatus")
    public AjaxResult jobStatus(String jobId, String jobName) {
        SlaveServerJobStatus status = sdk.jobStatus(jobId, jobName);

        return AjaxResult.ok().data(status);
    }

    @GetMapping("jobImage")
    public AjaxResult jobImage(String jobId, String jobName, HttpServletResponse response) throws IOException {
        ResultData<byte[]> rs = sdk.jobImage(jobId, jobName);

        if(rs.isSuccess()){
            return AjaxResult.ok().data(rs.getData());
        }

        return AjaxResult.err().msg(rs.getMessage());

    }
}
