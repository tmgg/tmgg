
package io.tmgg.sys.monitor.service;

import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.sys.vo.SysOnlineUserVo;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.core.log.LogManager;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import io.tmgg.web.session.MySessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统组织机构service接口实现类
 */
@Service
public class SysOnlineUserService {

    @Resource
    SysUserService sysUserService;

    @Resource
    MySessionRepository mySessionRepository;

    public Page<SysOnlineUserVo> findAll(Pageable pageable) {
        List<SysOnlineUserVo> voList = new ArrayList<>();

        List<Subject> subjectList = SecurityUtils.findAll();

        List<String> userIds = subjectList.stream().map(Subject::getId).collect(Collectors.toList());
        List<SysUser> userList = sysUserService.findAllById(userIds);
        Map<String, SysUser> userMap = userList.stream().collect(Collectors.toMap(BaseEntity::getId, t -> t));


        for (Subject subject: subjectList){
            SysUser user = userMap.get(subject.getId());

            SysOnlineUserVo vo = new SysOnlineUserVo();
            BeanUtil.copyProperties(user, vo);
            vo.setSessionId(subject.getToken());

            voList.add(vo);
        }

        return new PageImpl<>(voList, pageable, voList.size());
    }


    public void forceExist(String sessionId) {
        Assert.hasText(sessionId, "sessionId不能为空");
        //获取缓存的key
        String key = sessionId;

        Subject user = SecurityUtils.getCachedSubjectByToken(key);

        //如果缓存的用户存在，清除会话，否则表示该会话信息已失效，不执行任何操作
        if (ObjectUtil.isNotNull(user)) {
            //创建退出登录日志
            LogManager.me().saveLogoutLog(user.getAccount());
        }

        mySessionRepository.deleteById(sessionId);
    }
}
