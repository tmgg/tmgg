
package io.tmgg.sys.service;

import io.tmgg.sys.SysDictTreeNode;
import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.dao.SysDictItemDao;
import io.tmgg.sys.entity.SysDict;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class SysDictService extends BaseService<SysDict> {

    @Resource
    private SysDictItemDao sysDictItemDao;

    @Resource
    private SysDictDao sysDictDao;



    public List<SysDictTreeNode> tree() {
        List<SysDictTreeNode> resultList = CollectionUtil.newArrayList();

        JpaQuery<SysDict> query = new JpaQuery<>();
        List<SysDict> typeList = this.findAll(query);

        for (SysDict sysDict : typeList) {
            SysDictTreeNode sysDictTreeNode = new SysDictTreeNode();
            BeanUtil.copyProperties(sysDict, sysDictTreeNode);
            sysDictTreeNode.setPid(null);
            resultList.add(sysDictTreeNode);
        }

        JpaQuery<SysDictItem> dictQueryWrapper = new JpaQuery<>();

        List<SysDictItem> dictData = sysDictItemDao.findAll(dictQueryWrapper, Sort.by(SysDictItem.Fields.seq));
        for (SysDictItem sysDictItem : dictData) {
            SysDictTreeNode sysDictTreeNode = new SysDictTreeNode();
            sysDictTreeNode.setId(sysDictItem.getId());
            sysDictTreeNode.setPid(sysDictItem.getSysDict().getId());
            sysDictTreeNode.setCode(sysDictItem.getKey());
            sysDictTreeNode.setName(sysDictItem.getText());
            sysDictTreeNode.setColor(sysDictItem.getColor());
            resultList.add(sysDictTreeNode);
        }
        return  TreeTool.buildTree(resultList);
    }


}
