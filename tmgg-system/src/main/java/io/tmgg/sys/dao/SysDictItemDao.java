
package io.tmgg.sys.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.sys.entity.SysDictItem;
import io.tmgg.web.consts.CommonConstant;
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
public class SysDictItemDao extends BaseDao<SysDictItem> {

    @Transactional
    public void deleteByTypeId(String typeId) {
        JpaQuery<SysDictItem> query = new JpaQuery<>();

        query.eq(SysDictItem.Fields.typeId, typeId);

        List<SysDictItem> all = this.findAll(query);


        this.deleteAll(all);

    }

    public void deleteByTypeIdPhysical(String typeId) {
        deleteAll(new JpaQuery<>().eq(SysDictItem.Fields.typeId, typeId));
    }

    public List<Dict> getDictDataListByDictTypeId(String dictTypeId) {
        //构造查询条件
        JpaQuery<SysDictItem> queryWrapper = new JpaQuery<>();
        queryWrapper.eq(SysDictItem.Fields.typeId, dictTypeId);
        List<SysDictItem> results = this.findAll(queryWrapper, Sort.by(SysDictItem.Fields.sort));

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
