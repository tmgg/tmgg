package io.tmgg.modules.system;

import cn.hutool.core.date.DateUtil;
import io.tmgg.config.external.UserMessageProvider;
import io.tmgg.flowable.FlowableManager;
import io.tmgg.flowable.bean.TaskVo;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 框架右上角的消息提醒
 */
@Component
public class FrameworkUserMessageProvider implements UserMessageProvider {

    /**
     * 消息分类， 如 系统消息，通知公告，待办信息等
     *
     * @return
     */
    @Override
    public String userMsgType() {
        return "任务";
    }

    @Resource
    FlowableManager fm;



    @Override
    public int count() {
        long count = fm.taskTodoCount();

        return (int) count;
    }

    @Override
    public List<UserMessageVo> list()  {
        Page<TaskVo> page = fm.taskTodoList( PageRequest.ofSize( 20));
        List<TaskVo> taskList = page.getContent();

        List<UserMessageVo> list = taskList.stream().map(task -> {
            UserMessageVo vo = new UserMessageVo();
            vo.setTitle(task.getTaskName());
            vo.setCreateTime(DateUtil.formatDateTime(task.getCreateTime()));
            return vo;
        }).collect(Collectors.toList());

        return list;
    }


}
