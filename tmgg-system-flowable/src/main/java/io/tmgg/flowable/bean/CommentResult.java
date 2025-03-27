package io.tmgg.flowable.bean;

import io.tmgg.flowable.mgmt.service.MyTaskService;
import io.tmgg.lang.SpringTool;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.flowable.engine.task.Comment;

@Data
public class CommentResult {

    String id;
    String content;

    String time;

    String user;

    public CommentResult(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getFullMessage();
        this.time = DateFormatUtils.format(comment.getTime(), "yyyy-MM-dd HH:mm:ss") ;
        this.user = SpringTool.getBean(MyTaskService.class).getUserName(comment.getUserId());
    }
}
