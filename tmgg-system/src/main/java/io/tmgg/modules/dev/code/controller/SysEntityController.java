package io.tmgg.modules.dev.code.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.service.JpaService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
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
    public AjaxResult page( String searchText) throws IOException {
        List<String> list = tool.findAllNames();

        List<Dict> voList = list.stream().map(clsName -> {
            Dict map = new Dict();
            try {
                Class<?> cls = Class.forName(clsName);
                map.put("id",cls.getName());
                map.put("name", cls.getName());
                map.put("simpleName", cls.getSimpleName());
                map.put("superclassSimpleName", cls.getSuperclass().getSimpleName());
                map.put("remark", RemarkTool.getRemark(cls));

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            return map;
        }).collect(Collectors.toList());

        if(StrUtil.isNotEmpty(searchText)){
            voList = voList.stream().filter(s->StrUtil.containsIgnoreCase(s.getStr("name"), searchText) || StrUtil.containsIgnoreCase(s.getStr("remark"),searchText)).collect(Collectors.toList());
        }

        return AjaxResult.ok().data(new PageImpl<>(voList));
    }


}
