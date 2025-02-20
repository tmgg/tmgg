package io.tmgg.weixin.service;

import io.tmgg.weixin.entity.WeixinUser;

import java.util.Map;

public interface WeappAuthListener {


    /**
     *
     * @param weixinUser
     * @param model 返回给前端的数据
     */
   void onAfterLogin(WeixinUser weixinUser, Map<String, Object> model);

}
