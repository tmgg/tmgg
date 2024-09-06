package io.tmgg.weapp.service;

import io.tmgg.weapp.entity.WeappUser;

import java.util.Map;

public interface WeappAuthListener {


    /**
     *
     * @param weappUser
     * @param model 返回给前端的数据
     */
   void onAfterLogin(WeappUser weappUser, Map<String, Object> model);

}
