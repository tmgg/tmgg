package io.tmgg.kettle.controller;

import cn.hutool.core.lang.Dict;
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

    @RequestMapping("upload")
    public AjaxResult uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String xml = new String(bytes, StandardCharsets.UTF_8);

        KettleFile kettleFile = new KettleFile();
        kettleFile.setFileName(file.getOriginalFilename());
        kettleFile.setContent(xml);

        JobXmlInfo info = XmlTool.xmlToBean(xml, JobXmlInfo.class);
        kettleFile.setJobName(info.getName());
        kettleFile.setDescription(info.getDescription());
        List<JobXmlInfo.Parameter> parameters = info.getParameters();
        String json = JsonTool.toJsonQuietly(parameters);
        List<KettleFile.Parameter> parameters2 = JsonTool.jsonToBeanListQuietly(json, KettleFile.Parameter.class);
        kettleFile.setParameterList(parameters2);

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
        kettleFileService.deleteById(id);

        return AjaxResult.ok();
    }


    @GetMapping("options")
    public AjaxResult options() {
        List<KettleFile> list = kettleFileService.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        List<Option> options = new ArrayList<>();

        for (KettleFile f : list) {
            String jobName = f.getJobName();
            String desc = jobName + " (" + f.getDescription() + ")";

            List<KettleFile.Parameter> parameterList = f.getParameterList();
            List<Dict> data = parameterList.stream().map(p -> {
                Dict d = new Dict();
                d.put("key", p.getName());
                d.put("value", p.getDefaultValue());
                return d;
            }).collect(Collectors.toList());

            options.add(new Option(desc, jobName, data));
        }

        return AjaxResult.ok().data(options);
    }
}
