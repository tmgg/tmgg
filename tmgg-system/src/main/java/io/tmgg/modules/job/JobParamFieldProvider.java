package io.tmgg.modules.job;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.tmgg.lang.ann.field.Field;

import java.util.List;
import java.util.Map;

public interface JobParamFieldProvider {

    List<Field> getFields(JobDesc jobDesc, Map<String,Object> jobData) throws JsonProcessingException;

}
