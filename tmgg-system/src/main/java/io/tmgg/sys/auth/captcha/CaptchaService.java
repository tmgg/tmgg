package io.tmgg.sys.auth.captcha;

import io.tmgg.sys.auth.captcha.core.CaptchaVo;
import io.tmgg.sys.auth.captcha.core.PuzzleCaptcha;
import io.tmgg.sys.auth.captcha.core.VerifyParam;
import io.tmgg.sys.service.SysFileService;
import cn.hutool.core.img.ImgUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Slf4j
@Component
public class CaptchaService {

    Cache<String, Integer> cache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();

    Cache<String, Boolean> clientResultCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();

    @Resource
    private SysFileService sysFileService;


    /**
     * 获取验证码
     *
     *
     */
    public CaptchaVo captcha(String clientId, PuzzleCaptcha puzzleCaptcha) {
        String bgUrl = ImgUtil.toBase64DataUri(puzzleCaptcha.getArtwork(), "png");
        String puzzleUrl = ImgUtil.toBase64DataUri(puzzleCaptcha.getVacancy(), "png");


        log.info("clientId:{}", clientId);
        // TODO 调整为url模式



        // 偏移量
        cache.put(clientId, puzzleCaptcha.getX());
        CaptchaVo captchaVo = new CaptchaVo(bgUrl, puzzleUrl);
        return captchaVo;
    }


    /**
     * 验证码验证
     *
     * @param param
     *
     */
    public boolean verify(VerifyParam param) {
        int x = param.getX();
        String clientId = param.getClientId();


        Integer realX = cache.getIfPresent(clientId);
        Assert.notNull(realX, "验证码已失效");


        // 误差大于x个像素就返回false
        int diff = Math.abs(realX - x);
        if (diff > 30) {
            return false;
        }

        clientResultCache.put(clientId,true);
        return true;
    }

    public boolean isClientVerifyOK(String clientId){
        Boolean ifPresent = clientResultCache.getIfPresent(clientId);
        return  ifPresent != null && ifPresent;
    }


}
