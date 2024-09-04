
package io.tmgg.sys.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统在线用户结果集
 *
 */
@Data
public class SysOnlineUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String name;

    /**
     * 最后登陆IP
     */
    private String lastLoginIp;


    private Date lastAccessedTime;
    private Date expireTime;

    /**
     * 最后登陆地址
     */
    private String lastLoginAddress;

    /**
     * 最后登陆所用浏览器
     */
    private String lastLoginBrowser;

    /**
     * 最后登陆所用系统
     */
    private String lastLoginOs;

}
