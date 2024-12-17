
package io.tmgg.sys.service;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.obj.Option;
import io.tmgg.sys.controller.SysDictTreeNode;
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


    public String findTextByDictCodeAndKey(String code, String key){
       return sysDictItemDao.findTextByDictCodeAndKey(code, key);
    }

    public List<Option> findOptions(String code){
        List<SysDictItem> list = sysDictItemDao.findAllByDictCode(code);

        return Option.convertList(list, BaseEntity::getId, SysDictItem::getText);
    }


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
        for (SysDictItem item : dictData) {
            SysDictTreeNode node = new SysDictTreeNode();
            node.setId(item.getId());
            node.setPid(item.getSysDict().getId());
            node.setCode(getFinalKey(item.getSysDict(), item));
            node.setName(item.getText());
            node.setColor(item.getColor());
            resultList.add(node);
        }
        return  TreeTool.buildTree(resultList);
    }

    public Object getFinalKey(SysDict dict, SysDictItem item){
        if(dict.getIsNumber()!= null && dict.getIsNumber()){
            return Integer.parseInt( item.getKey());
        }
        return item.getKey();
    }

}
