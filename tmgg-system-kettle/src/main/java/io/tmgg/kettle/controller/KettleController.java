package io.tmgg.kettle.controller;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.response.SlaveServerJobStatus;
import io.github.tmgg.kettle.sdk.response.SlaveServerStatus;
import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.kettle.service.KettleFileService;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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


}
