package io.tmgg.flowable.assignment.impl;


import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class AssigneeUserProvider implements AssignmentTypeProvider {
    @Resource
    FlowableMasterDataProvider masterDataProvider;

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }



    @Override
    public String getCode() {
        return "AssigneeUser";
    }

    @Override
    public String getLabel() {
        return "分配给单个用户";
    }



    @Override
    public Collection<Identity> findAll() {
        Map<String, String> userMap = masterDataProvider.getUserMap();

        List<Identity> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            Identity identity = new Identity(k, null, v, true, true);
            result.add(identity);
        }

        return result;
    }

    @Override
    public String getLabelById(String userId) {
        return masterDataProvider.getUserNameById(userId);
    }

    @Override
    public XmlAttribute getXmlAttribute() {
        return XmlAttribute.assignee;
    }



}
