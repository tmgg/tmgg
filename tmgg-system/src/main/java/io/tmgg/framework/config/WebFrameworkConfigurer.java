package io.tmgg.framework.config;

import io.tmgg.modules.system.entity.SysConfig;
import io.tmgg.modules.system.entity.SysDict;
import io.tmgg.modules.system.entity.SysDictItem;
import io.tmgg.modules.system.entity.SysMenu;

import java.util.List;

/**
 * 框架的配置入口
 * 参考webmvcConfigurer 的形式
 *  TODO
 */
public interface WebFrameworkConfigurer {

    default void configureSiteInfo(SiteInfoConfigurer configurer) {
    }

    /**
     * 添加系统菜单实体
     */
    default void addSysMenu(List<SysMenu> registry) {

    }

    /**
     * 添加系统配置实体
     */
    default void addSysConfig(List<SysConfig> registry) {

    }

    /**
     * 添加数据字典实体
     */
    default void addSysDict(List<SysDict> registry, List<SysDictItem> itemRegistry) {

    }

    /***
     * 设置预留字段的显示文本
     */
    default void configureEntityExtraField(List<EntityExtraFieldConfigurer> registry){
        //registry.add(new EntityExtraFieldConfigurer(SysMenu.class,"extra1", "预留字段1"));
    }

}
