
package io.tmgg.sys.log.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.log.entity.SysOpLog;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 系统访问日志mapper
 *

 *
 */
@Repository
public class SysOpLogDao extends BaseDao<SysOpLog> {

    public List<SysOpLog> findByTimeLessThan(Date date) {
        JpaQuery<SysOpLog> q = JpaQuery.create();
        q.le(SysOpLog.FIELD_CREATE_TIME, date);
        return this.findAll(q);
    }

}
