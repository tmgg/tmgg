
package io.tmgg.sys.service;

import io.tmgg.sys.SysDictTreeNode;
import io.tmgg.sys.dao.SysDictItemDao;
import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.entity.SysDict;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.TreeTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class SysDictService extends BaseService<SysDict> {

    @Resource
    private SysDictItemDao dictDataDao;


    @Resource
    private SysDictDao dictTypeDao;


    public SysDict findByCode(String typeCode) {

        return this.findOne(new JpaQuery<>().eq(SysDict.Fields.code, typeCode));
    }


    public SysDict saveOrUpdateByCode(String code, String name) {
        SysDict type = this.findByCode(code);
        if (type == null) {
            type = new SysDict();
            type.setCode(code);
            type.setName(name);
            type = dictTypeDao.save(type);
        } else if (!StrUtil.equals(name, type.getName())) {
            type.setName(name);
            type = dictTypeDao.save(type);
        }
        return type;
    }

    @Transactional
    public void initByFile(String code, String name, String file) {
        SysDict type = this.saveOrUpdateByCode(code, name);
        dictDataDao.deleteByTypeIdPhysical(type.getId());

        String content = ResourceUtil.readUtf8Str(file);
        List<String> lines = StrUtil.splitTrim(content, "\n");

        for (String line : lines) {
            SysDictItem dictData = new SysDictItem();
            dictData.setTypeId(type.getId());
            dictData.setCode(line);
            dictData.setValue(line);
            dictData.setStatus(CommonStatus.ENABLE);
            dictDataDao.save(dictData);
        }
    }





    @Transactional(rollbackFor = Exception.class)
    public void delete(SysDict SysDict) {
        this.deleteById(SysDict.getId());
        //级联删除字典值
        dictDataDao.deleteByTypeIdPhysical(SysDict.getId());
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

        List<SysDictItem> dictData = dictDataDao.findAll(dictQueryWrapper, Sort.by(SysDictItem.Fields.sort));
        for (SysDictItem sysDictItem : dictData) {
            SysDictTreeNode sysDictTreeNode = new SysDictTreeNode();
            sysDictTreeNode.setId(sysDictItem.getId());
            sysDictTreeNode.setPid(sysDictItem.getTypeId());
            sysDictTreeNode.setCode(sysDictItem.getCode());
            sysDictTreeNode.setName(sysDictItem.getValue());
            sysDictTreeNode.setColor(sysDictItem.getColor());
            resultList.add(sysDictTreeNode);
        }
        return  TreeTool.buildTree(resultList);
    }


}
