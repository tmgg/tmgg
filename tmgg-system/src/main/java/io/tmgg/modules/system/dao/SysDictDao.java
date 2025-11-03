
package io.tmgg.modules.system.dao;

import io.tmgg.data.repository.BaseDao;
import io.tmgg.data.query.JpaQuery;
import io.tmgg.modules.system.entity.SysDict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class SysDictDao extends BaseDao<SysDict> {



    @Transactional
    public SysDict add(String code,String text){
        SysDict  dict = new SysDict();
        dict.setCode(code);
        dict.setText(text);
        dict = this.save(dict);
        return dict;
    }

    public SysDict findByCode(String code) {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.eq(SysDict.Fields.code, code);
        return this.findOne(q);
    }
    public boolean existsByCode(String code) {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.eq("code",code);
        return this.exists(q);
    }

    public SysDict findByIdOrCode(String code) {
        JpaQuery<SysDict> q = new JpaQuery<>();
        q.addSubOr(qq->{
            qq.eq(SysDict.Fields.code, code);
            qq.eq("id", code);
        });

        return this.findOne(q);
    }


}
