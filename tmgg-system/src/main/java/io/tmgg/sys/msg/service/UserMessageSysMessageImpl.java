package io.tmgg.sys.msg.service;

import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.lang.PastTimeFormatTool;
import io.tmgg.config.external.UserMessageProvider;
import io.tmgg.sys.msg.result.MsgVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserMessageSysMessageImpl implements UserMessageProvider {

    @Resource
    SysMsgService sysMsgService;

    @Override
    public String userMsgType() {
        return "消息";
    }



    @Override
    public int count() {
        String id = SecurityUtils.getSubject().getId();
        return (int) sysMsgService.countNewMsg(id);
    }

    @Override
    public List<UserMessageVo> list() {
        String id = SecurityUtils.getSubject().getId();
        Page<MsgVo> page = sysMsgService.page(PageRequest.of(0, 10), id, false);


        List<UserMessageVo> voList = new ArrayList<>();

        for (MsgVo m : page) {
            UserMessageVo vo = new UserMessageVo();

            vo.setTitle(m.getTitle());
            vo.setContent(m.getContent());
            vo.setCreateTime(PastTimeFormatTool.formatPastTime(m.getCreateTime()));

            voList.add(vo);
        }
        return voList;
    }


}
