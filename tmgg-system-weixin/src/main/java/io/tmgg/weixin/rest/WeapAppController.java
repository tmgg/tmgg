package io.tmgg.weixin.rest;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.weixin.WexinTool;
import io.tmgg.weixin.entity.WeixinUser;
import io.tmgg.weixin.service.WeappAuthListener;
import io.tmgg.weixin.service.WeappUserService;
import cn.hutool.core.img.ImgUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 小程序端请求
 */
@Slf4j
@RequestMapping("app/weapp")
@RestController
public class WeapAppController {


    @Resource
    WxMaService wxMaService;

    @Resource
    WeappUserService weappUserService;

    @Autowired(required = false)
    WeappAuthListener weappAuthListener;



    /**
     * 登陆接口
     */
    @PostMapping("login")
    public AjaxResult login(String code) {
        if (StringUtils.isBlank(code)) {
            return AjaxResult.err().msg("微信登录，code不能为空");
        }

        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openId = session.getOpenid();
            WeixinUser user = null;

            synchronized (openId) {
                user = weappUserService.findByOpenId(openId);
                if (user == null) {
                    user = new WeixinUser();
                    user.setOpenId(openId);
                    user.setAppId(WexinTool.curAppId());
                    user.setUnionId(session.getUnionid());
                }
                user.setSessionKey(session.getSessionKey());
                user.setLastLoginTime(new Date());
                user = weappUserService.save(user);
            }

            //生成token
            Map<String, Object> model = new HashMap<>();
            model.put("user", user);

            if (weappAuthListener != null) {
                weappAuthListener.onAfterLogin(user, model);
            }

            return AjaxResult.ok().msg("登录成功").data( model);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return AjaxResult.err().msg(e.getMessage());
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    /**
     * 获取用户绑定手机号信息
     */
    @PostMapping("decryptPhone")
    public AjaxResult decryptPhone(String code, HttpSession session) throws WxErrorException {
        String userId = WexinTool.curUserId(session);
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(code);

        WeixinUser user = weappUserService.findOne(userId);
        user.setPhone(phoneNoInfo.getPurePhoneNumber());
        weappUserService.save(user);

        return AjaxResult.ok().data(phoneNoInfo.getPurePhoneNumber());
    }


    /**
     * 解密用户
     *
     * @param encryptedData
     * @param iv
     * @param session
     * @return
     */
    @PostMapping("decryptUser")
    public AjaxResult decryptUser(String encryptedData, String iv, HttpSession session) {
        WeixinUser weixinUser = WexinTool.curUser(session);
        String sessionKey = weixinUser.getSessionKey();

        // 解密
        WxMaUserInfo wxMaUserInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        weixinUser.setNickName(wxMaUserInfo.getNickName());
        weixinUser.setAvatarUrl(wxMaUserInfo.getAvatarUrl());

        weixinUser = weappUserService.save(weixinUser);


        return AjaxResult.ok().data(weixinUser);
    }


    /**
     * 获取数据库里面的用户
     *
     * @return
     */
    @GetMapping("getUserInfo")
    public AjaxResult getUserInfo(HttpSession session) {
        String id = WexinTool.curUserId(session);
        WeixinUser user = weappUserService.findOne(id);
        return AjaxResult.ok().data(user);
    }


    /**
     * 更新用户
     *
     * @return
     */
    @PostMapping("updateUser")
    public AjaxResult updateUser(WeixinUser weixinUser, HttpSession session) {
        String id = WexinTool.curUserId(session);
        WeixinUser user = weappUserService.updateNickName(id, weixinUser);
        return AjaxResult.ok().data(user);
    }

    @PostMapping("uploadAvatar")
    public AjaxResult uploadAvatar(MultipartFile file, HttpSession session) throws IOException {
        BufferedImage image = ImgUtil.read(file.getInputStream());

        String png = ImgUtil.toBase64DataUri(image, "png");

        String id = WexinTool.curUserId(session);

        WeixinUser user = weappUserService.updateAvatar(id, png);

        return AjaxResult.ok().data(user);
    }

}
