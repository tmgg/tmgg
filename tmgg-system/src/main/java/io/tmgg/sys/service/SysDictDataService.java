
package io.tmgg.sys.service;

import io.tmgg.sys.dao.SysDictDao;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.sys.entity.SysDictType;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.obj.Option;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统字典值
 */
@Service
@CacheConfig(cacheNames = "dict_data")
public class SysDictDataService extends BaseService<SysDictItem> {

    @Resource
    SysDictDao dictTypeDao;


    public Map<String, String> getMap(String typeCode) {
        List<SysDictItem> list = this.getDictDataListByDictTypeCode(typeCode);

        Map<String, String> map = new LinkedHashMap<>();

        for (SysDictItem data : list) {
            map.put(data.getCode(), data.getValue());
        }

        return map;
    }

    @Cacheable(cacheNames = "getValue")
    public String getValue(String typeCode, Object dataCode) {
        if (dataCode == null) {
            return null;
        }
        dataCode = String.valueOf(dataCode);

        JpaQuery typeQuery = new JpaQuery<>()
                .eq(SysDictType.Fields.code, typeCode);
        SysDictType type = dictTypeDao.findOne(typeQuery);
        if(type == null){
            return  null;
        }

        JpaQuery dataQuery = new JpaQuery<>()
                .eq(SysDictItem.Fields.typeId, type.getId())
                .eq(SysDictItem.Fields.code, dataCode)
                .eq(SysDictItem.Fields.status, CommonStatus.ENABLE);
        SysDictItem data = baseDao.findOne(dataQuery);
        if (data != null) {
            return data.getValue();
        }
        return null;
    }

    public String getCodeByValue(String typeCode, String dataValue) {
        SysDictType type = dictTypeDao.findOne(new JpaQuery<>().eq(SysDictType.Fields.code, typeCode));

        SysDictItem data = baseDao.findOne(new JpaQuery<>().eq(SysDictItem.Fields.typeId, type.getId()).eq(SysDictItem.Fields.value, dataValue));
        if (data != null) {
            return data.getValue();
        }
        return null;
    }

    public Page<SysDictItem> page(SysDictItem dictData, Pageable pageable) {

        //构造条件
        JpaQuery<SysDictItem> queryWrapper = new JpaQuery<>();
        if (ObjectUtil.isNotNull(dictData)) {
            //根据字典类型查询
            if (ObjectUtil.isNotEmpty(dictData.getTypeId())) {
                queryWrapper.eq(SysDictItem.Fields.typeId, dictData.getTypeId());
            }
            //根据字典值的编码模糊查询
            if (ObjectUtil.isNotEmpty(dictData.getCode())) {
                queryWrapper.like(SysDictItem.Fields.code, dictData.getCode());
            }
            //根据字典值的内容模糊查询
            if (ObjectUtil.isNotEmpty(dictData.getValue())) {
                queryWrapper.like(SysDictItem.Fields.value, dictData.getValue());
            }
        }
        //返回分页查询结果
        return this.findAll(queryWrapper, pageable);
    }


    public List<SysDictItem> list(SysDictItem dictData) {
        //构造条件,查询某个字典类型下的
        JpaQuery<SysDictItem> queryWrapper = new JpaQuery<>();
        if (ObjectUtil.isNotNull(dictData)) {
            if (ObjectUtil.isNotEmpty(dictData.getTypeId())) {
                queryWrapper.eq(SysDictItem.Fields.typeId, dictData.getTypeId());
            }
        }
        return this.findAll(queryWrapper, Sort.by(SysDictItem.Fields.sort));
    }


    @CacheEvict(cacheNames = {"getDictDataListByDictTypeCode","getOptions","getValue"}, allEntries = true)
    public SysDictItem add(SysDictItem SysDictItem) {
        //校验参数，检查是否存在重复的编码，不排除当前添加的这条记录
        checkParam(SysDictItem, false);

        //将dto转为实体
        SysDictItem sysDictItem = new SysDictItem();
        BeanUtil.copyProperties(SysDictItem, sysDictItem);

        //设置状态为启用
        sysDictItem.setStatus(CommonStatus.ENABLE);
        sysDictItem.setValue(sysDictItem.getValue().trim());

        this.save(sysDictItem);
        return sysDictItem;
    }

    @CacheEvict(cacheNames = {"getDictDataListByDictTypeCode","getOptions","getValue"}, allEntries = true)
    public void delete(SysDictItem SysDictItem) {
        SysDictItem sysDictItem = this.findOne(SysDictItem.getId());


        //更新实体
        this.save(sysDictItem);
    }

    @CacheEvict(cacheNames = {"getDictDataListByDictTypeCode","getOptions","getValue"}, allEntries = true)
    public void edit(SysDictItem dictData) {
        SysDictItem sysDictItem = this.findOne(dictData.getId());

        //校验参数，检查是否存在重复的编码或者名称，排除当前编辑的这条记录
        checkParam(dictData, true);

        //请求参数转化为实体
        BeanUtil.copyProperties(dictData, sysDictItem, SysDictItem.Fields.status);

        sysDictItem.setValue(sysDictItem.getValue().trim());

        this.save(sysDictItem);
    }

    @Cacheable(cacheNames = "getOptions")
    public List<Option> getOptions(String typeCode) {
        List<SysDictItem> list = this.getDictDataListByDictTypeCode(typeCode);
        return Option.convertList(list, SysDictItem::getCode, SysDictItem::getValue);
    }


    @Cacheable(cacheNames = "getDictDataListByDictTypeCode")
    public List<SysDictItem> getDictDataListByDictTypeCode(String typeCode) {
        SysDictType type = dictTypeDao.findOne(new JpaQuery<>().eq(SysDictType.Fields.code, typeCode));
        Assert.state(type != null, "字典类型编码" + typeCode + "不存在");

        JpaQuery<SysDictItem> queryWrapper = new JpaQuery<>();

        queryWrapper.eq(SysDictItem.Fields.typeId, type.getId());


        return this.findAll(queryWrapper, Sort.by(SysDictItem.Fields.sort));
    }









    @Transactional
    public void changeStatus(SysDictItem SysDictItem) {
        SysDictItem sysDictItem = this.findOne(SysDictItem.getId());
        CommonStatus status = SysDictItem.getStatus();
        sysDictItem.setStatus(status);
    }


    /**
     * 校验参数，校验是否存在相同的编码
     *

 *
     */
    private void checkParam(SysDictItem sysDictItem, boolean isExcludeSelf) {
        String id = sysDictItem.getId();
        String typeId = sysDictItem.getTypeId();
        String code = sysDictItem.getCode();

        //构建带code的查询条件
        JpaQuery<SysDictItem> queryWrapper = new JpaQuery<>();
        queryWrapper.eq(SysDictItem.Fields.typeId, typeId)
                .eq(SysDictItem.Fields.code, code)
                ;

        //如果排除自己，则增加查询条件主键id不等于本条id
        if (isExcludeSelf) {
            queryWrapper.ne("id", id);
        }

        //查询重复记录的数量
        long countByCode = this.count(queryWrapper);
        Assert.state(countByCode < 1, "编码重复");
    }

}
