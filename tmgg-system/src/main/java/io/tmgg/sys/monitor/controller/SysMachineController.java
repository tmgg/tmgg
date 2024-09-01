
package io.tmgg.sys.monitor.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.monitor.service.SysMachineService;
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
