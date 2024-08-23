package io.tmgg.code.controller;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ann.RemarkTool;
import io.tmgg.lang.dao.JpaTool;
import io.tmgg.lang.obj.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("code/entity")
public class SysEntityController {

    public static final String[] EX = {
            "cn.crec.job.",
            "cn.crec.sys.",
            "cn.crec.web."
    };



    @RequestMapping("page")
    public AjaxResult page() throws IOException {
        List<String> list = JpaTool.findAllEntity();

        // 排除系统的实体
        list = list.stream().filter(e -> !StrUtil.startWithAny(e, EX)).sorted().collect(Collectors.toList());

        List<Map<String, Object>> voList = list.stream().map(clsName -> {
            Map<String, Object> map = new HashMap<>();

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

        return AjaxResult.success(new PageImpl<>(voList));
    }


}
