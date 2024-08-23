package io.tmgg.flowable.assignment;

import cn.moon.lang.web.SpringTool;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;

@Service
public class AssignmentService {



    public String getObjectName(String type, String value) {
        Collection<AssignmentTypeProvider> values = SpringTool.getBeansOfType(AssignmentTypeProvider.class).values();

        AssignmentTypeProvider provider = values.stream().filter(p -> p.getCode().equals(type)).findFirst().orElse(null);
        Assert.state(provider != null, "类型" + type +"的provider不能为空， 请实现AssignmentTypeProvider");

       return provider.getLabelById(value);
    }



}
