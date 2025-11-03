package io.tmgg.modules.system.dto.mapper;

import io.tmgg.modules.system.dao.SysOrgDao;
import io.tmgg.modules.system.dto.UserDto;
import io.tmgg.modules.system.entity.SysUser;
import jakarta.annotation.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Resource
    SysOrgDao orgDao;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "unitLabel", source = "unitId", qualifiedByName = "getOrgName")
    @Mapping(target = "deptLabel", source = "deptId", qualifiedByName = "getOrgName")
    public abstract UserDto toDto(SysUser input);

    @Named("getOrgName")
    protected String getOrgName(String orgId) {
        return orgDao.getNameById(orgId);
    }

}
