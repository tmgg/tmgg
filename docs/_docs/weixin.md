---
title: 微信集成
layout: doc
---


支持多小程序

小程序用户登录，信息获取，手机号获取等基本接口。





```
  @Slf4j
  @RequestMapping("app/weixin/mini")
  @RestController
  public class WeixinAppMiniController {


    /**
     * 登陆接口
     */
    @PostMapping("login")
    public AjaxResult login(String code, HttpSession httpSession) {
        
    }


    /**
     * 获取用户绑定手机号信息
     */
    @PostMapping("decryptPhone")
    public AjaxResult decryptPhone(String code, HttpSession session) throws WxErrorException {
      
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
    }


    /**
     * 获取数据库里面的用户
     */
    @GetMapping("getUserInfo")
    public AjaxResult getUserInfo(HttpSession session) 


    @PostMapping("updateUser")
    public AjaxResult updateUser(WeixinUser weixinUser, HttpSession session) 

    @PostMapping("uploadAvatar")
    public AjaxResult uploadAvatar(MultipartFile file, HttpSession session)

}
```




# 监听用户登录、等
实现 WexinAuthListener 即可
