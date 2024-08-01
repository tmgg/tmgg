package io.tmgg.job.dao;

import io.tmgg.job.entity.SysJobLogging;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class SysJobLoggingDao extends BaseDao<SysJobLogging> {
    public void deleteByJobId(String jobId) {
        JpaQuery<SysJobLogging> q = new JpaQuery<>();
        q.eq(SysJobLogging.Fields.jobId, jobId);
        List<SysJobLogging> list = findAll(q);
        this.deleteAll(list);
    }


    /**
     * 清理日志
     * <p>
     * 由于删除数量大，jpa默认都会查一遍，比较费内存
     * 使用JPQL删除小于该时间的数据
     */
    @Transactional
    public void clean(Date date) {
        Query q = em.createQuery("delete from SysJobLogging where timeStamp  <= " + date.getTime());
        q.executeUpdate();
    }
}
