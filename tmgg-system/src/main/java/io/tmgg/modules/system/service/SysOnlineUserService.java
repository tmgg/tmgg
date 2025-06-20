
package io.tmgg.modules.system.service;

import cn.hutool.core.date.DateUtil;
import io.tmgg.modules.system.vo.SysOnlineUserVo;
import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.web.perm.Subject;
import io.tmgg.framework.session.SysHttpSession;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    SysHttpSessionService sysHttpSessionService;





    public Page<SysOnlineUserVo> findAll(Pageable pageable) {
        List<SysOnlineUserVo> voList = new ArrayList<>();

        List<SysHttpSession> sessionList = sysHttpSessionService.findAll();

        for (SysHttpSession session : sessionList) {
            Subject subject = session.getAttribute(SysHttpSession.SUBJECT_KEY);
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
        sysHttpSessionService.forceExistBySessionId(sessionId);
    }
}
