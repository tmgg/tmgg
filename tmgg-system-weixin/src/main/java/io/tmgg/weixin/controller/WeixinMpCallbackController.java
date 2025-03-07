package io.tmgg.weixin.controller;

import io.micrometer.common.util.StringUtils;
import io.tmgg.lang.ann.PublicRequest;
import jakarta.annotation.PostConstruct;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.web.bind.annotation.*;

/**
 * 微信公众号回调通知
 */
@RestController
@RequestMapping("app/weixin/callback")
public class WeixinMpCallbackController {

    WxMpDefaultConfigImpl config;
    WxMpService wxMpService;
    WxMpMessageRouter wxMpMessageRouter;

    /**
     * 微信回调的
     * @param signature
     * @param nonce
     * @param timestamp
     * @param token
     * @param echostr
     * @param encryptType
     * @param msgSignature
     * @param body
     * @return
     */
    @PublicRequest
    @GetMapping("msg")
    public String ok(String signature, String nonce, String timestamp, String token, String echostr,
                     @RequestParam(name = "encrypt_type", defaultValue = "raw") String encryptType,
                     @RequestParam(name = "msg_signature") String msgSignature,
                     @RequestBody String body
    ) {
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            return "非法请求";
        }

        if (StringUtils.isNotBlank(echostr)) {
            return echostr;
        }

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(body);
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            if (outMessage == null) {
                //为null，说明路由配置有问题，需要注意
                return "";
            }
            return outMessage.toXml();
        }

        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(body, wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            if (outMessage == null) {
                //为null，说明路由配置有问题，需要注意
                return "";
            }
            return outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
        }

        return ("不可识别的加密类型");
    }


    @PostConstruct
    private void init() {
        config = new WxMpDefaultConfigImpl();
        config.setAppId("..."); // 设置微信公众号的appid
        config.setSecret("..."); // 设置微信公众号的app corpSecret
        config.setToken("..."); // 设置微信公众号的token
        config.setAesKey("..."); // 设置微信公众号的EncodingAESKey

        wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);

        WxMpMessageHandler handler = (wxMessage, map, wxMpService, wxSessionManager) -> {
            WxMpXmlOutTextMessage m
                    = WxMpXmlOutMessage.TEXT().content("测试加密消息").fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser()).build();
            return m;
        };

        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        wxMpMessageRouter
                .rule()
                .async(false)
                .content("哈哈") // 拦截内容为“哈哈”的消息
                .handler(handler)
                .end();
    }

}
