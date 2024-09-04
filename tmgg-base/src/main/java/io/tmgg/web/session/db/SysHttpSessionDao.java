package io.tmgg.web.session.db;

import io.tmgg.lang.dao.BaseDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SysHttpSessionDao extends BaseDao<SysHttpSession> {


    @Transactional
    public void invalidate(String sessionId) {
        SysHttpSession session = this.findOne(sessionId);
        session.setInvalidated(true);
        this.save(session);
    }


}
