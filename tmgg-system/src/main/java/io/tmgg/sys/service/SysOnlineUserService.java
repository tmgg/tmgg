
package io.tmgg.sys.service;

import cn.hutool.core.date.DateUtil;
import io.tmgg.core.log.LogManager;
import io.tmgg.lang.DateTool;
import io.tmgg.lang.DurationTool;
import io.tmgg.lang.PastTimeFormatTool;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.sys.vo.SysOnlineUserVo;
import io.tmgg.web.perm.SecurityManager;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.session.db.SysHttpSession;
import io.tmgg.web.session.db.SysHttpSessionDao;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.DurationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.session.MapSession;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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

        for (SysHttpSession session : sessionList) {
            Subject subject = session.getAttribute(SecurityManager.SESSION_KEY);
            if( session.isExpired() || subject == null){
                continue;
            }

            SysOnlineUserVo vo = new SysOnlineUserVo();


            vo.setSessionId(session.getId());
            vo.setAccount(subject.getAccount());
            vo.setName(subject.getName());
            vo.setLastAccessedTime(DateUtil.date(session.getLastAccessedTime()));
            vo.setExpireTime(DateUtils.addSeconds(DateUtil.date(session.getLastAccessedTime()), (int) session.getMaxInactiveInterval().toSeconds()) );
            vo.setExpired(session.isExpired());
            voList.add(vo);
        }


        return new PageImpl<>(voList, pageable, voList.size());
    }


    public void forceExist(String sessionId) {
        Assert.hasText(sessionId, "sessionId不能为空");
        securityManager.forceExistBySessionId(sessionId);
    }
}