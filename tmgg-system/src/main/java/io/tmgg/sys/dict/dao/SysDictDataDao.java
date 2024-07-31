
package io.tmgg.sys.dict.dao;

import io.tmgg.sys.dict.entity.SysDictData;
import io.tmgg.web.consts.CommonConstant;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统字典值
 *

 *
 */
@Repository
public class SysDictDataDao extends BaseDao<SysDictData> {

    public void findByTypeIdAndCode(String typeId,String code) {
        JpaQuery<SysDictData> query = new JpaQuery<>();

        query.eq(SysDictData.Fields.typeId, typeId);

        List<SysDictData> all = this.findAll(query);


        this.deleteAll(all);

    }
    @Transactional
    public void deleteByTypeId(String typeId) {
        JpaQuery<SysDictData> query = new JpaQuery<>();

        query.eq(SysDictData.Fields.typeId, typeId);

        List<SysDictData> all = this.findAll(query);


        this.deleteAll(all);

    }

    public void deleteByTypeIdPhysical(String typeId) {
        deleteAll(new JpaQuery<>().eq(SysDictData.Fields.typeId, typeId));
    }

    public List<Dict> getDictDataListByDictTypeId(String dictTypeId) {
        //构造查询条件
        JpaQuery<SysDictData> queryWrapper = new JpaQuery<>();
        queryWrapper.eq(SysDictData.Fields.typeId, dictTypeId).ne(SysDictData.Fields.status, CommonStatus.DELETED);
        List<SysDictData> results = this.findAll(queryWrapper, Sort.by(SysDictData.Fields.sort));

        //抽取code和value封装到map返回
        List<Dict> dictList = CollectionUtil.newArrayList();
        results.forEach(sysDictData -> {
            Dict dict = Dict.create();
            dict.put(CommonConstant.CODE, sysDictData.getCode());
            dict.put(CommonConstant.VALUE, sysDictData.getValue());
            dictList.add(dict);
        });

        return dictList;
    }
}
