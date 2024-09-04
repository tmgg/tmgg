package io.tmgg.web.session.db;

import com.mysql.cj.x.protobuf.MysqlxConnection;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SysHttpSessionDao extends BaseDao<SysHttpSession> {
    public SysHttpSession findBySessionId(String sessionId) {
        return this.findOneByField(SysHttpSession.Fields.sessionId, sessionId);
    }

    @Transactional
    public void deleteBySessionId(String id) {
        SysHttpSession session = this.findBySessionId(id);
        this.delete(session);
    }


}
