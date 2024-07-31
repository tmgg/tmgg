
package io.tmgg.sys.log.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.sys.log.entity.SysVisLog;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 系统访问日志mapper
 *
 *
 */
@Repository
public class SysVisLogDao extends BaseDao<SysVisLog> {


    public List<SysVisLog> findByTimeLessThan(Date date) {
        JpaQuery<SysVisLog> q = JpaQuery.create();
        q.le(SysVisLog.FIELD_CREATE_TIME, date);
        return this.findAll(q);
    }


}
