
package io.tmgg.core.consts;

/**
 * 系统管理异常枚举编码构成常量
 * <p>
 * 异常枚举编码由3部分组成，如下：
 * <p>
 * 模块编码（2位） + 分类编码（4位） + 具体项编码（至少1位）
 * <p>
 * 模块编码和分类编码在ExpEnumCodeConstant类中声明
 *
 */
public interface SysExpEnumConstant {

    /**
     * 模块分类编码（2位）
     * <p>
     * snowy-system模块异常枚举编码
     */
    int SNOWY_SYS_MODULE_EXP_CODE = 20;

    /* 分类编码（4位） */

    /**
     * 系统参数配置相关异常枚举
     */
    int SYS_CONFIG_EXCEPTION_ENUM = 1200;

    /**
     * 文件信息表相关枚举
     */
    int SYS_FILE_INFO_EXCEPTION_ENUM = 1500;

    /**
     * 系统菜单相关异常枚举
     */
    int SYS_MENU_EXCEPTION_ENUM = 1600;

    /**
     * 系统组织机构相关异常枚举
     */
    int SYS_ORG_EXCEPTION_ENUM = 1700;

    /**
     * 系统角色相关异常枚举
     */
    int SYS_ROLE_EXCEPTION_ENUM = 1900;

    /**
     * 系统用户相关异常枚举
     */
    int SYS_USER_EXCEPTION_ENUM = 2000;

}
