
package io.tmgg.sys.service;

import io.tmgg.sys.SysDictTreeNode;
import io.tmgg.sys.dao.SysDictItemDao;
import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.sys.entity.SysDictType;
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

/**
 * 系统字典类型service接口实现类
 */
@Service
public class SysDictTypeService extends BaseService<SysDictType> {

    @Resource
    private SysDictItemDao dictDataDao;


    @Resource
    private SysDictDao dictTypeDao;


    public SysDictType findByCode(String typeCode) {

        return this.findOne(new JpaQuery<>().eq(SysDictType.Fields.code, typeCode));
    }


    public SysDictType saveOrUpdateByCode(String code, String name) {
        SysDictType type = this.findByCode(code);
        if (type == null) {
            type = new SysDictType();
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
        SysDictType type = this.saveOrUpdateByCode(code, name);
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


    public List<Dict> dropDown(SysDictType SysDictType) {
        JpaQuery<SysDictType> query = new JpaQuery<>()
                .eq(io.tmgg.sys.entity.SysDictType.Fields.code, SysDictType.getCode());

        SysDictType sysDictType = this.findOne(query);
        Assert.state(sysDictType != null, "字典类型不存在");
        return dictDataDao.getDictDataListByDictTypeId(sysDictType.getId());
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(SysDictType SysDictType) {
        this.deleteById(SysDictType.getId());
        //级联删除字典值
        dictDataDao.deleteByTypeIdPhysical(SysDictType.getId());
    }


    public List<SysDictTreeNode> tree() {
        List<SysDictTreeNode> resultList = CollectionUtil.newArrayList();

        JpaQuery<SysDictType> query = new JpaQuery<>();
        List<SysDictType> typeList = this.findAll(query);

        for (SysDictType sysDictType : typeList) {
            SysDictTreeNode sysDictTreeNode = new SysDictTreeNode();
            BeanUtil.copyProperties(sysDictType, sysDictTreeNode);
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
