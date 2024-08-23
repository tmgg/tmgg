package io.tmgg.weapp.app;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.weapp.WeappTool;
import io.tmgg.weapp.entity.WeappUser;
import io.tmgg.weapp.service.WeappBizService;
import io.tmgg.weapp.service.WeappUserService;
import cn.hutool.core.img.ImgUtil;
import io.tmgg.web.token.TokenManger;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.ImageUtils;
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
    WeappBizService weappBizService;

    @Resource
    TokenManger tokenManger;


    /**
     * 登陆接口
     */
    @PostMapping("login")
    public AjaxResult login(String code) {
        if (StringUtils.isBlank(code)) {
            return AjaxResult.error("微信登录，code不能为空");
        }

        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openId = session.getOpenid();
            WeappUser user = null;

            synchronized (openId) {
                user = weappUserService.findByOpenId(openId);
                if (user == null) {
                    user = new WeappUser();
                    user.setOpenId(openId);
                    user.setAppId(WeappTool.curAppId());
                    user.setUnionId(session.getUnionid());
                }
                user.setSessionKey(session.getSessionKey());
                user.setLastLoginTime(new Date());
                user = weappUserService.save(user);
            }

            //生成token
            String token = tokenManger.createToken(user.getId());
            Map<String, Object> model = new HashMap<>();
            model.put("token", token);
            model.put("user", user);

            if (weappBizService != null) {
                weappBizService.afterLogin(user, model);

            }

            return AjaxResult.success("登录成功", model);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error(e.getMessage());
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    /**
     * 获取用户绑定手机号信息
     */
    @PostMapping("decryptPhone")
    public AjaxResult decryptPhone(String code) throws WxErrorException {
        String userId = WeappTool.curUserId();
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(code);

        WeappUser user = weappUserService.findOne(userId);
        user.setPhone(phoneNoInfo.getPurePhoneNumber());
        weappUserService.save(user);

        return AjaxResult.success(phoneNoInfo.getPurePhoneNumber());
    }


    /**
     * 解密用户
     *
     * @param encryptedData
     * @param iv
     * @return
     */
    @PostMapping("decryptUser")
    public AjaxResult decryptUser(String encryptedData, String iv) {
        WeappUser weappUser = WeappTool.curUser();
        String sessionKey = weappUser.getSessionKey();

        // 解密
        WxMaUserInfo wxMaUserInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        weappUser.setNickName(wxMaUserInfo.getNickName());
        weappUser.setAvatarUrl(wxMaUserInfo.getAvatarUrl());

        weappUser = weappUserService.save(weappUser);


        return AjaxResult.success(weappUser);
    }


    /**
     * 获取数据库里面的用户
     *
     * @return
     */
    @GetMapping("getUserInfo")
    public AjaxResult getUserInfo() {
        String id = WeappTool.curUserId();
        WeappUser user = weappUserService.findOne(id);
        return AjaxResult.success(user);
    }


    /**
     * 更新用户
     *
     * @return
     */
    @PostMapping("updateUser")
    public AjaxResult updateUser(WeappUser weappUser) {
        String id = WeappTool.curUserId();
        WeappUser user = weappUserService.updateNickName(id, weappUser);
        return AjaxResult.success(user);
    }

    @PostMapping("uploadAvatar")
    public AjaxResult uploadAvatar(MultipartFile file) throws IOException {
        BufferedImage image = ImgUtil.read(file.getInputStream());

        String png = ImgUtil.toBase64DataUri(image, "png");

        String id = WeappTool.curUserId();

        WeappUser user = weappUserService.updateAvatar(id, png);

        return AjaxResult.success(user);
    }

}
