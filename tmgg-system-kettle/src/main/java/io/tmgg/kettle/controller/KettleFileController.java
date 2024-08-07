package io.tmgg.kettle.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.response.SlaveServerJobStatus;
import io.github.tmgg.kettle.sdk.response.SlaveServerStatus;
import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.kettle.service.KettleFileService;
import io.tmgg.lang.JsonTool;
import io.tmgg.lang.XmlTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("kettle/file")
public class KettleFileController {

    @Resource
    KettleFileService kettleFileService;

    @Resource
    KettleSdk sdk;

    @RequestMapping("upload")
    public AjaxResult uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String xml = new String(bytes, StandardCharsets.UTF_8);

        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());


        KettleFile kettleFile = new KettleFile();
        kettleFile.setFileName(file.getOriginalFilename());
        kettleFile.setContent(xml);
        kettleFile.setFileType(suffix);

        if (suffix.equals("kjb")) { // 作业
            JobXmlInfo info = XmlTool.xmlToBean(xml, JobXmlInfo.class);
            kettleFile.setName(info.getName());
            kettleFile.setDescription(info.getDescription());
            List<JobXmlInfo.Parameter> parameters = info.getParameters();
            String json = JsonTool.toJsonQuietly(parameters);
            List<KettleFile.Parameter> parameters2 = JsonTool.jsonToBeanListQuietly(json, KettleFile.Parameter.class);
            kettleFile.setParameterList(parameters2);
        } else if (suffix.equals("ktr")) { // 转换

        }




        kettleFileService.save(kettleFile);

        return AjaxResult.ok();
    }

    @RequestMapping("list")
    public AjaxResult listFile() {
        List<KettleFile> list = kettleFileService.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        return AjaxResult.ok().data(list);
    }

    @RequestMapping("delete")
    public AjaxResult deleteFile(String id) {

        KettleFile file = kettleFileService.findOne(id);

        try {
            SlaveServerStatus status = sdk.status();
            List<SlaveServerJobStatus> jobStatusList = status.getJobStatusList();
            if (jobStatusList != null) {
                for (SlaveServerJobStatus jobStatus : jobStatusList) {
                    String jobName = jobStatus.getJobName();
                    Assert.state(file.getName().equals(jobName), "作业在Carte服务中存在，请先删除作业");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 失败的情况允许删除
        }

        kettleFileService.deleteById(id);

        return AjaxResult.ok();
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<KettleFile> list = kettleFileService.findJobList();
        List<Option> options = new ArrayList<>();

        for (KettleFile f : list) {
            String jobName = f.getName();
            String label = jobName;
            if(StrUtil.isNotBlank(f.getDescription())){
                 label = jobName + " (" + f.getDescription() + ")";
            }


            List<Dict> data = new ArrayList<>();
            List<KettleFile.Parameter> parameterList = f.getParameterList();
            if(parameterList != null){
               data = parameterList.stream().map(p -> {
                    Dict d = new Dict();
                    d.put("key", p.getName());
                    d.put("value", p.getDefaultValue());
                    return d;
                }).collect(Collectors.toList());
            }


            options.add(new Option(label, jobName, data));
        }

        return AjaxResult.ok().data(options);
    }
}
