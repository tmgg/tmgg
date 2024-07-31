package io.tmgg.config.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单脚本数据提供器
 */
public interface MenuBadgeProvider {

    List<MenuBadge> getData();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class MenuBadge {
        String menuCode;
        int badge;
    }


}
