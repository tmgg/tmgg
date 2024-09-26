package io.tmgg.job;


import io.tmgg.data.Field;

import java.util.List;

public interface JobParamFieldProvider {

    List<Field> getFields(JobDesc jobDesc);

}
