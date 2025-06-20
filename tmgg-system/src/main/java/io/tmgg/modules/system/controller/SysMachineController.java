
package io.tmgg.modules.system.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.service.SysMachineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 系统属性监控控制器
 *
 */
@RestController
public class SysMachineController {

    @Resource
    private SysMachineService sysMachineService;

    /**
     * 系统属性监控
     *

 *
     */
    @GetMapping("/sysMachine/query")
    public AjaxResult query() {
        return AjaxResult.ok().data(sysMachineService.query());
    }
}
