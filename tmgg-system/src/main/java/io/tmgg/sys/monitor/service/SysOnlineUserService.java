
package io.tmgg.sys.monitor.service;

import cn.hutool.core.date.DateUtil;
import io.tmgg.core.log.LogManager;
import io.tmgg.lang.DurationTool;
import io.tmgg.lang.PastTimeFormatTool;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.sys.vo.SysOnlineUserVo;
import io.tmgg.web.perm.SecurityManager;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.DurationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.session.MapSession;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统组织机构service接口实现类
 */
@Service
public class SysOnlineUserService {


    @Resource
    SecurityManager securityManager;


    @Resource
    SysHttpSessionDao sysHttpSessionDao;


    public Page<SysOnlineUserVo> findAll(Pageable pageable) {
        List<SysOnlineUserVo> voList = new ArrayList<>();

        List<SysHttpSession> sessionList = sysHttpSessionDao.findAll();

        for (SysHttpSession sysHttpSession : sessionList) {
            MapSession session = sysHttpSession.getSession();
            Subject subject = session.getAttribute(SecurityManager.SESSION_KEY);
            if(sysHttpSession.getInvalidated() || subject == null){
                continue;
            }

            SysOnlineUserVo vo = new SysOnlineUserVo();


            vo.setSessionId(session.getId());
            vo.setAccount(subject.getAccount());
            vo.setName(subject.getName());
            vo.setLastAccessedTime(sysHttpSession.getLastAccessedTime());
            vo.setMaxInactiveInterval(DurationTool.format(sysHttpSession.getMaxInactiveInterval()));
            voList.add(vo);
        }


        return new PageImpl<>(voList, pageable, voList.size());
    }


    public void forceExist(String sessionId) {
        Assert.hasText(sessionId, "sessionId不能为空");
        Subject user = securityManager.findBySessionId(sessionId);
        String account = user.getAccount();

        securityManager.forceExistBySessionId(sessionId);
        LogManager.me().saveLogoutLog(account);
    }
}
