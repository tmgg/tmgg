package io.tmgg.framework.session;

import io.tmgg.lang.dao.BaseDao;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SysHttpSessionDao extends BaseDao<SysHttpSession> {


    @Async
    @Transactional
    public void cleanExpired(){
        List<SysHttpSession> sessionList = this.findAll();

        for (SysHttpSession session : sessionList) {
            if(session.isExpired()){
                this.deleteById(session.getId());
            }
        }
    }
}
