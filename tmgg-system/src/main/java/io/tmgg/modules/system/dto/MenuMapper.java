package io.tmgg.modules.system.dto;

import io.tmgg.modules.system.dto.response.MenuResponse;
import io.tmgg.modules.system.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "id", target = "key")
    MenuResponse menuToResponse(SysMenu menu);

    List<MenuResponse> menuToResponseList(List<SysMenu> menuList);
}
