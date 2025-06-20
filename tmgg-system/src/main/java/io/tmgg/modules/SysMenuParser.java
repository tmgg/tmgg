package io.tmgg.modules;

import io.tmgg.modules.system.entity.SysMenu;

import java.util.Collection;

public interface SysMenuParser {

    Collection<SysMenu> parseMenuList() throws Exception;
}
