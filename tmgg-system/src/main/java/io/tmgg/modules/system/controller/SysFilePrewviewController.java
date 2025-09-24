
package io.tmgg.modules.system.controller;

import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.modules.system.service.SysFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文件
 */
@Slf4j
@Controller
@RequestMapping("sysFile")
public class SysFilePrewviewController {

    @Resource
    private SysFileService service;


    /**
     * 可以加后缀， 这样对某些设备友好
     * 支持的格式
     * /sysFile/preview/123
     * /sysFile/preview/123.jpg (增加后缀，对浏览器等客户端友好)
     * /sysFile/preview/202508/123.jpg （原始对象路径，可方便直接使用nginx反向代理）
     *
     * @param id
     * @throws Exception
     */
    @PublicRequest
    @GetMapping(value = {"preview/{id}", "preview/{id}.{suffix}", "preview/{dir}/{id}.{suffix}"})
    public ResponseEntity<InputStreamResource> preview(@PathVariable String id, @PathVariable(required = false) String suffix, @PathVariable(required = false) String dir, Integer w) throws Exception {
        return service.preview(id, w);
    }


}
