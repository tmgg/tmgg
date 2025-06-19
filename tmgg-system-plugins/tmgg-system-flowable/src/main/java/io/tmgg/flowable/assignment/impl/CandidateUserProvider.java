package io.tmgg.flowable.assignment.impl;


import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.flowable.assignment.AssignmentTypeProvider;
import io.tmgg.flowable.assignment.Identity;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class CandidateUserProvider implements AssignmentTypeProvider {

    @Resource
    FlowableMasterDataProvider masterDataProvider;
    @Override
    public int getOrder() {
        return 2;
    }


    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public String getCode() {
        return "CandidateUser";
    }

    @Override
    public String getLabel() {
        return "候选用户";
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
    public String getLabelById(String id) {
        if(id.contains(",")){
            String[] ids = id.split(",");
            StringBuilder sb = new StringBuilder();
            for(String str: ids){
                if(StringUtils.isNotEmpty(str)){

                    String name = masterDataProvider.getUserNameById(str);
                    sb.append(name).append("、");
                }
            }
            if(sb.length() >0){
                sb.deleteCharAt(sb.length()-1);
            }
            return sb.toString();
        }
        return masterDataProvider.getUserNameById(id);
    }

    @Override
    public XmlAttribute getXmlAttribute() {
        return XmlAttribute.candidateUsers;
    }



}
