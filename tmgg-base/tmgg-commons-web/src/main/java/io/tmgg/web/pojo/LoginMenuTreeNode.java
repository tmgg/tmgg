
package io.tmgg.web.pojo;

import lombok.Data;

/**
 * 登录菜单
 *
 *
 *
 */
@Data
public class LoginMenuTreeNode {

    /**
     * id
     */
    private String id;

    /**
     * 父id
     */
    private String pid;

    /**
     * 路由名称, 必须设置,且不能重名
     */
    private String name;


    /**
     * 重定向地址, 访问这个路由时, 自定进行重定向
     */
    private String redirect;

    /**
     * 路由元信息（路由附带扩展信息）
     */
    private Meta meta;

    /**
     * 路径
     */
    private String path;

    /**
     * 控制路由和子路由是否显示在 sidebar
     */
    private boolean hidden;

    /**
     * 路由元信息内部类
     */
    @Data
    public static class Meta {

        /**
         * 路由标题, 用于显示面包屑, 页面标题 *推荐设置
         */
        public String title;

        /**
         * 图标
         */
        public String icon;

        /**
         * 是否可见
         */
        public boolean show;

        /**
         * 如需外部打开，增加：_blank
         */
        public String target;


    }

}
