package io.tmgg.modules.job.dao;

import io.tmgg.dbtool.DbTool;
import io.tmgg.modules.job.entity.SysJobLogging;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Query;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class SysJobLoggingDao extends BaseDao<SysJobLogging> {
    @Resource
    DbTool db;

    public void deleteByJobId(String jobId) {
        db.executeQuietly("delete from sys_job_logging where job_id=?", jobId);
    }

    /**
     * 由于数量大，直接执行sql，以免内存爆掉
     * @param ids
     */
    public void deleteByJobLogId(List<String> ids) {
        JpaQuery<SysJobLogging> q = new JpaQuery<>();
        q.in(SysJobLogging.Fields.jogLogId, ids);
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
