
package io.tmgg.modules.sys;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.TreeManager;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Route;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.modules.sys.service.SysMenuBadgeService;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.modules.sys.service.SysRoleService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


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
