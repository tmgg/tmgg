package ${modulePackageName}.controller;

import ${modulePackageName}.entity.${name};
{modulePackageName}.service.${name}Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;

@RestController
@RequestMapping("${firstLowerName}")
public class ${name}Controller extends BaseController<${name}> {

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

