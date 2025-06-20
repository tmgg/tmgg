package io.tmgg.modules.system.controller;

import cn.hutool.core.collection.CollUtil;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.entity.SysManual;
import io.tmgg.modules.system.service.SysManualService;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("sysManual")
public class SysManualController extends BaseController<SysManual> {

    @Resource
    SysManualService service;


    /**
     * 用户点击帮助按钮
     *
     * @param searchText
     * @param pageable
     * @return
     * @throws Exception
     */
    @PublicRequest
    @RequestMapping("pageForUser")
    public AjaxResult pageForUser(String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = {"name", "version"}) Pageable pageable) throws Exception {
        JpaQuery<SysManual> q = new JpaQuery<>();

        q.searchText(searchText, "name");


        List<SysManual> list = service.findAll(q, pageable.getSort());
        // 数据量不大，直接内存过滤吧

        Map<String,SysManual> rs = new HashMap<>();
        for (SysManual e : list) {
            if(!rs.containsKey(e.getName())){
                rs.put(e.getName(),e);
            }
        }

        Collection<SysManual> values = rs.values();


        return AjaxResult.ok().data(new PageImpl<>(new ArrayList<>(values)));
    }

}

