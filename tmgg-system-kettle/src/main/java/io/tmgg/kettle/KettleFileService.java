package io.tmgg.kettle;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.tmgg.kettle.controller.JobXmlInfo;
import io.tmgg.lang.XmlTool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KettleFileService {

    @Resource
    KettleSdk sdk;

    public List<JobXmlInfo.Parameter> getJobParameters(String jobId) throws JsonProcessingException {
        String xml = sdk.getRepositoryObjectContent(jobId);
        JobXmlInfo info = XmlTool.xmlToBean(xml, JobXmlInfo.class);

        List<JobXmlInfo.Parameter> parameters = info.getParameters();

        return parameters;
    }
}
