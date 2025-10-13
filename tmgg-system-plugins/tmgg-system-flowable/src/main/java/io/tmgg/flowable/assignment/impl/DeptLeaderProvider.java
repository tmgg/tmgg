package io.tmgg.flowable.assignment.impl;

import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static io.tmgg.flowable.FlowableManager.VAR_DEPT_LEADER;

@Component
public class DeptLeaderProvider implements AssignmentTypeProvider {

    @Override
    public String getCode() {
        return "deptLeader";
    }

    @Override
    public String getLabel() {
        return "部门领导";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public String getLabelById(String id) {
        return "部门领导";
    }

    @Override
    public XmlAttribute getXmlAttribute() {
        return XmlAttribute.assignee;
    }

    @Override
    public Collection<Identity> findAll() {
        Identity identity = new Identity("${"+ VAR_DEPT_LEADER +"}", null, "部门领导", true, true);
        return List.of(identity);
    }
}
