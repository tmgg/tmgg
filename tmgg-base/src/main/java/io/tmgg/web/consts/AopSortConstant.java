
package io.tmgg.web.consts;

/**
 * aop顺序的常量
 * <p>
 * 顺序越小越靠前
 *
 *
 *
 */
public interface AopSortConstant {

    /**
     * 全局异常拦截器
     */
    int GLOBAL_EXP_HANDLER_AOP = -120;

    /**
     * 接口资源权限校验
     */
    int PERMISSION_AOP = -100;

    /**
     * 业务日志的AOP
     */
    int BUSINESS_LOG_AOP = 100;

}
