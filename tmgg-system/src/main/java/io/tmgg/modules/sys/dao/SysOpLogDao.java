
package io.tmgg.modules.sys.dao;

import io.tmgg.lang.SpringTool;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.modules.sys.entity.SysLog;
import org.springframework.stereotype.Repository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统访问日志mapper
 */
@Repository
public class SysOpLogDao extends BaseDao<SysLog> {

    public void saveAsync(SysLog sysVisLog) {
        executorService.schedule(() -> {
            SpringTool.getBean(SysOpLogDao.class).save(sysVisLog);
        }, 5, TimeUnit.SECONDS);

    }

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
}
