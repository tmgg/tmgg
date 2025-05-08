package io.tmgg.modules.api.service;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.modules.api.dao.ApiAccessLogDao;
import io.tmgg.modules.api.entity.ApiAccessLog;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.modules.api.entity.ApiResource;
import jakarta.annotation.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApiAccessLogService extends BaseService<ApiAccessLog> {

    @Resource
    ApiAccessLogDao dao;

    public void add(ApiAccount account, ApiResource resource, String requestId,  Map<String, Object> params, Object retValue,String ip, long executionTime) {
        ApiAccessLog a = new ApiAccessLog();
        a.setRequestId(requestId);
        a.setName(resource.getName());
        a.setAction(resource.getAction());
        a.setRequestData(JsonTool.toJsonQuietly(params));
        a.setResponseData(JsonTool.toJsonQuietly(retValue));
        a.setIp(ip);


        // a.setIpLocation(ip);

        a.setExecutionTime(executionTime);


        a.setAccountName(account.getName());

        dao.save(a);
    }
}

