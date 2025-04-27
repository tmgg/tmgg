package io.tmgg.modules.code.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ann.MsgTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.service.JpaService;
import io.tmgg.web.CommonQueryParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("code/entity")
public class SysEntityController {

    @Resource
    JpaService tool;


    @RequestMapping("page")
    public AjaxResult page(@RequestBody CommonQueryParam param) throws IOException {
        String keyword = param.getKeyword();
        List<String> list = tool.findAllNames();

        List<Dict> voList = list.stream().map(clsName -> {
            Dict map = new Dict();
            try {
                Class<?> cls = Class.forName(clsName);
                map.put("id",cls.getName());
                map.put("name", cls.getName());
                map.put("simpleName", cls.getSimpleName());
                map.put("superclassSimpleName", cls.getSuperclass().getSimpleName());
                map.put("remark", MsgTool.getMsg(cls));

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            return map;
        }).collect(Collectors.toList());

        if(StrUtil.isNotEmpty(keyword)){
            voList = voList.stream().filter(s->StrUtil.containsIgnoreCase(s.getStr("name"), keyword) || StrUtil.containsIgnoreCase(s.getStr("remark"),keyword)).collect(Collectors.toList());
        }

        return AjaxResult.ok().data(new PageImpl<>(voList));
    }


}
