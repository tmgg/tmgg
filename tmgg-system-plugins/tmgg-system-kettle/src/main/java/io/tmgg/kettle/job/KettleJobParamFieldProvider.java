package io.tmgg.kettle.job;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.lang.field.Field;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobParamFieldProvider;
import io.tmgg.kettle.KettleFileService;
import io.tmgg.kettle.controller.JobXmlInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.tmgg.kettle.job.KettleJob.JOB_PARAM_FILE;

@Component
public class KettleJobParamFieldProvider implements JobParamFieldProvider {

    @Resource
    KettleFileService kettleFileService;

    @Override
    public List<Field> getFields(JobDesc jobDesc, Map<String, Object> jobData) throws JsonProcessingException {
        List<Field> list = new ArrayList<>();

        Field field = new Field();
        field.setLabel("作业文件");
        field.setName(JOB_PARAM_FILE);
        field.setRequired(true);
        field.setComponentType("remoteSelect");

        Map<String, Object> props = new HashMap<>();
        props.put("url", "/kettle/file/options");

        field.setComponentProps(props);

        list.add(field);

        if (jobData.containsKey(JOB_PARAM_FILE)) {
            String file = (String) jobData.get(JOB_PARAM_FILE);
            List<JobXmlInfo.Parameter> jobParameters = kettleFileService.getJobParameters(file);
            if (jobParameters != null) {
                for (JobXmlInfo.Parameter jobParameter : jobParameters) {
                    String name = jobParameter.getName();
                    String defaultValue = jobParameter.getDefaultValue();
                    String description = jobParameter.getDescription();

                    Field f = new Field();
                    f.setLabel(name);
                    f.setName(name);

                    String placeholder = "";

                    Map<String, Object> componentProps = new HashMap<>();
                    if (StrUtil.isNotEmpty(defaultValue)) {
                        placeholder = "默认值:" + defaultValue + " ";
                    }
                    if(StrUtil.isNotEmpty(description)){
                        placeholder += description;
                    }

                    componentProps.put("placeholder", placeholder);

                    f.setComponentProps(componentProps);

                    list.add(f);
                }
            }
        }

        return list;
    }
}
