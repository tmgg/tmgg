
package io.tmgg.modules.system;

import cn.hutool.core.codec.Base64;
import io.tmgg.lang.obj.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * 通用工具
 * 某些前端不好处理的，放到后端处理后返回
 */
@Slf4j
@RestController
@RequestMapping("utils")
public class CommonUtilsController {


    /**
     * 获取当前登录信息
     */
    @PostMapping("fileBase64")
    public AjaxResult getLoginUser(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String encode = Base64.encode(bytes);

        return AjaxResult.ok().data(encode);
    }







}
