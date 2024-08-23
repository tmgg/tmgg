package io.tmgg.sys.auth.captcha;

import io.tmgg.lang.ResourceTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.SystemProperties;
import io.tmgg.sys.auth.captcha.core.CaptchaVo;
import io.tmgg.sys.auth.captcha.core.PuzzleCaptcha;
import io.tmgg.sys.auth.captcha.core.VerifyParam;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * 验证码
 *
 */
@RestController
@RequestMapping("/captcha")
@Validated
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @jakarta.annotation.Resource
    SystemProperties systemProperties;


    @PublicApi
    @GetMapping( "get")
    public AjaxResult captcha(@RequestParam String clientId) throws IOException {
       // String path = "images/captcha/*";
        String path = systemProperties.getCaptchaFiles();

        Resource[] resourceList = ResourceTool.findAll(path);


        int i = RandomUtil.randomInt(resourceList.length);
        Resource imgPath = resourceList[i];


        InputStream is = imgPath.getInputStream();
        PuzzleCaptcha puzzleCaptcha = new PuzzleCaptcha(is);
        puzzleCaptcha.setWidth(640);
        puzzleCaptcha.setHeight(360);

        puzzleCaptcha.setImageQuality(Image.SCALE_AREA_AVERAGING);
        puzzleCaptcha.run();

        CaptchaVo captcha = captchaService.captcha(clientId, puzzleCaptcha);

        is.close();

        return AjaxResult.success(captcha);
    }

    @PublicApi
    @PostMapping( "verify")
    public AjaxResult verify(@RequestBody @Validated VerifyParam param) {

        boolean verify = captchaService.verify(param);

        return AjaxResult.success(verify);
    }
}
