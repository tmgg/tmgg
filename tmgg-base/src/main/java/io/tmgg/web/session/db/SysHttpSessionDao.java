package io.tmgg.web.session.db;

import io.tmgg.lang.dao.BaseDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SysHttpSessionDao extends BaseDao<SysHttpSession> {
    public SysHttpSession findBySessionId(String sessionId) {
        return this.findOneByField(SysHttpSession.Fields.sessionId, sessionId);
    }

    @Transactional
    public void invalidate(String sessionId) {
        SysHttpSession session = this.findBySessionId(sessionId);
        session.setInvalidated(true);
        this.save(session);
    }


}
