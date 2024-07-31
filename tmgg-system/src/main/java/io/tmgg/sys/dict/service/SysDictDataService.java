
package io.tmgg.sys.dict.service;

import io.tmgg.sys.dict.dao.SysDictTypeDao;
import io.tmgg.sys.dict.entity.SysDictData;
import io.tmgg.sys.dict.entity.SysDictType;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.exception.ServiceAssert;
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

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统字典值
 */
@Service
@CacheConfig(cacheNames = "dict_data")
public class SysDictDataService extends BaseService<SysDictData> {

    @Resource
    SysDictTypeDao dictTypeDao;


    public Map<String, String> getMap(String typeCode) {
        List<SysDictData> list = this.getDictDataListByDictTypeCode(typeCode);

        Map<String, String> map = new LinkedHashMap<>();

        for (SysDictData data : list) {
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
                .eq(SysDictData.Fields.typeId, type.getId())
                .eq(SysDictData.Fields.code, dataCode)
                .eq(SysDictData.Fields.status, CommonStatus.ENABLE);
        SysDictData data = baseDao.findOne(dataQuery);
        if (data != null) {
            return data.getValue();
        }
        return null;
    }

    public String getCodeByValue(String typeCode, String dataValue) {
        SysDictType type = dictTypeDao.findOne(new JpaQuery<>().eq(SysDictType.Fields.code, typeCode));

        SysDictData data = baseDao.findOne(new JpaQuery<>().eq(SysDictData.Fields.typeId, type.getId()).eq(SysDictData.Fields.value, dataValue));
        if (data != null) {
            return data.getValue();
        }
        return null;
    }

    public Page<SysDictData> page(SysDictData dictData, Pageable pageable) {

        //构造条件
        JpaQuery<SysDictData> queryWrapper = new JpaQuery<>();
        if (ObjectUtil.isNotNull(dictData)) {
            //根据字典类型查询
            if (ObjectUtil.isNotEmpty(dictData.getTypeId())) {
                queryWrapper.eq(SysDictData.Fields.typeId, dictData.getTypeId());
            }
            //根据字典值的编码模糊查询
            if (ObjectUtil.isNotEmpty(dictData.getCode())) {
                queryWrapper.like(SysDictData.Fields.code, dictData.getCode());
            }
            //根据字典值的内容模糊查询
            if (ObjectUtil.isNotEmpty(dictData.getValue())) {
                queryWrapper.like(SysDictData.Fields.value, dictData.getValue());
            }
        }
        //查询未删除的
        queryWrapper.ne(SysDictData.Fields.status, CommonStatus.DELETED);
        //返回分页查询结果
        return this.findAll(queryWrapper, pageable);
    }


    public List<SysDictData> list(SysDictData dictData) {
        //构造条件,查询某个字典类型下的
        JpaQuery<SysDictData> queryWrapper = new JpaQuery<>();
        if (ObjectUtil.isNotNull(dictData)) {
            if (ObjectUtil.isNotEmpty(dictData.getTypeId())) {
                queryWrapper.eq(SysDictData.Fields.typeId, dictData.getTypeId());
            }
        }
        //查询未删除的
        queryWrapper.ne(SysDictData.Fields.status, CommonStatus.DELETED);
        return this.findAll(queryWrapper, Sort.by(SysDictData.Fields.sort));
    }


    @CacheEvict(cacheNames = {"getDictDataListByDictTypeCode","getOptions","getValue"}, allEntries = true)
    public SysDictData add(SysDictData SysDictData) {
        //校验参数，检查是否存在重复的编码，不排除当前添加的这条记录
        checkParam(SysDictData, false);

        //将dto转为实体
        SysDictData sysDictData = new SysDictData();
        BeanUtil.copyProperties(SysDictData, sysDictData);

        //设置状态为启用
        sysDictData.setStatus(CommonStatus.ENABLE);
        sysDictData.setValue(sysDictData.getValue().trim());

        this.save(sysDictData);
        return sysDictData;
    }

    @CacheEvict(cacheNames = {"getDictDataListByDictTypeCode","getOptions","getValue"}, allEntries = true)
    public void delete(SysDictData SysDictData) {
        SysDictData sysDictData = this.findOne(SysDictData.getId());

        //逻辑删除，修改状态
        sysDictData.setStatus(CommonStatus.DELETED);

        //更新实体
        this.save(sysDictData);
    }

    @CacheEvict(cacheNames = {"getDictDataListByDictTypeCode","getOptions","getValue"}, allEntries = true)
    public void edit(SysDictData dictData) {
        SysDictData sysDictData = this.findOne(dictData.getId());

        //校验参数，检查是否存在重复的编码或者名称，排除当前编辑的这条记录
        checkParam(dictData, true);

        //请求参数转化为实体
        BeanUtil.copyProperties(dictData, sysDictData, SysDictData.Fields.status);

        sysDictData.setValue(sysDictData.getValue().trim());

        this.save(sysDictData);
    }

    @Cacheable(cacheNames = "getOptions")
    public List<Option> getOptions(String typeCode) {
        List<SysDictData> list = this.getDictDataListByDictTypeCode(typeCode);
        return Option.convertList(list, SysDictData::getCode, SysDictData::getValue);
    }


    @Cacheable(cacheNames = "getDictDataListByDictTypeCode")
    public List<SysDictData> getDictDataListByDictTypeCode(String typeCode) {
        SysDictType type = dictTypeDao.findOne(new JpaQuery<>().eq(SysDictType.Fields.code, typeCode));
        ServiceAssert.state(type != null, "字典类型编码" + typeCode + "不存在");

        JpaQuery<SysDictData> queryWrapper = new JpaQuery<>();

        queryWrapper.eq(SysDictData.Fields.typeId, type.getId()).ne(SysDictData.Fields.status, CommonStatus.DELETED);


        return this.findAll(queryWrapper, Sort.by(SysDictData.Fields.sort));
    }









    @Transactional
    public void changeStatus(SysDictData SysDictData) {
        SysDictData sysDictData = this.findOne(SysDictData.getId());
        CommonStatus status = SysDictData.getStatus();
        sysDictData.setStatus(status);
    }


    /**
     * 校验参数，校验是否存在相同的编码
     *

 *
     */
    private void checkParam(SysDictData sysDictData, boolean isExcludeSelf) {
        String id = sysDictData.getId();
        String typeId = sysDictData.getTypeId();
        String code = sysDictData.getCode();

        //构建带code的查询条件
        JpaQuery<SysDictData> queryWrapper = new JpaQuery<>();
        queryWrapper.eq(SysDictData.Fields.typeId, typeId)
                .eq(SysDictData.Fields.code, code)
                .ne(SysDictData.Fields.status, CommonStatus.DELETED);

        //如果排除自己，则增加查询条件主键id不等于本条id
        if (isExcludeSelf) {
            queryWrapper.ne("id", id);
        }

        //查询重复记录的数量
        long countByCode = this.count(queryWrapper);
        Assert.state(countByCode < 1, "编码重复");
    }

}
