package io.tmgg.system;

import io.tmgg.flowable.FlowableMasterDataProvider;
import io.tmgg.sys.org.service.SysOrgService;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.sys.user.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class MasterDataProviderImpl implements FlowableMasterDataProvider {
    @Resource
    SysUserService sysUserService;

    @Resource
    SysOrgService sysOrgService;

    @Override
    public String getUserNameById(String userId) {
        return sysUserService.getNameById(userId);
    }



    @Override
    public List<String> getDirectChildUnitIdArr(String unitId) {
        return sysOrgService.findDirectChildUnitIdArr(unitId);
    }

    @Override
    public Map<String, String> getUserMap() {
        List<SysUser> list = sysUserService.findValid();
        Map<String,String> map  = new LinkedHashMap<>();
        for (SysUser sysUser : list) {
            map.put(sysUser.getId(), sysUser.getName());
        }
        return map;
    }
}