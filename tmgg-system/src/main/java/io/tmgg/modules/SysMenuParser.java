package io.tmgg.modules;

import io.tmgg.modules.sys.entity.SysMenu;

import java.util.Collection;

public interface SysMenuParser {

    Collection<SysMenu> getMenuList() throws Exception;
}
