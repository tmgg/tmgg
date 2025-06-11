package io.tmgg.flowable.assignment.impl;


import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class StarterHandler implements AssignmentTypeProvider {


    @Override
    public int getOrder() {
        return 0;
    }


    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public String getCode() {
        return "starter";
    }

    @Override
    public String getLabel() {
        return "分配给发起人";
    }



    @Override
    public Collection<Identity> findAll() {
        return Arrays.asList( new Identity("${INITIATOR}",null, "发起人", true, true));
    }

    @Override
    public String getLabelById(String id) {
        return "发起人";
    }

    @Override
    public XmlAttribute getXmlAttribute() {
        return XmlAttribute.assignee;
    }




}
