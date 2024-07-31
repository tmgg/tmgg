
package io.tmgg.sys.app.dao;

import io.tmgg.sys.app.entity.SysApp;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统应用
 */
@Repository
public class SysAppDao extends BaseDao<SysApp> {

    public List<SysApp> findValid() {
        // 按激活排序
        Sort sort = Sort.by(Sort.Direction.DESC, SysApp.Fields.active);
        sort =  sort.and(Sort.by(Sort.Direction.ASC, SysApp.Fields.seq));

        JpaQuery<SysApp> q = new JpaQuery<>();
        q.eq(SysApp.Fields.status, CommonStatus.ENABLE);

        return this.findAll(sort);
    }

    public SysApp findByCode(String code) {
        JpaQuery<SysApp> q = new JpaQuery<>();
        q.eq(SysApp.Fields.code, code);

        return this.findOne(q);
    }
}
