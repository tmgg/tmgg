
package io.tmgg.sys.controller;

import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.sys.service.SysDictDataService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("sysDictItem")
public class SysDictItemController extends BaseCURDController<SysDictItem> {

    @Resource
    private SysDictDataService sysDictDataService;






}
