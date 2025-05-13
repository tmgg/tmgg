package ${modulePackageName}.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import ${modulePackageName}.entity.${name};
{modulePackageName}.service.${name}Service;
import io.tmgg.lang.persistence.BaseCURDController;
import io.tmgg.lang.persistence.BaseEntity;
import io.tmgg.lang.obj.TreeOption;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${firstLowerName}")
public class ${name}Controller extends BaseCURDController<${name}> {

    @Resource
    ${name}Service service;


    @GetMapping("tree")
    public AjaxResult tree() {
        List<${name}> list = service.findAll(Sort.by("seq"));

        List<TreeOption> treeList = list.stream().map(o -> {
        TreeOption treeOption = new TreeOption();
        treeOption.setTitle(o.getName());
        treeOption.setKey(o.getId());
        treeOption.setParentKey(o.getPid());

        return treeOption;
        }).collect(Collectors.toList());

        List<TreeOption> tree = TreeOption.convertTree(treeList);

        return AjaxResult.ok().data(tree);
    }


    @GetMapping("detail")
    public AjaxResult detail(String id) {
        ${name} data = service.findOne(id);

        return AjaxResult.ok().data(data);
    }


}

