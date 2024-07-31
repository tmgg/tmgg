package io.tmgg.sys.msg.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class MsgVo {

    private String id;

    private String title;

    private String content;

    private LocalDateTime createTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 状态（字典 0未读 1已读）
     */
    private Integer status;

    /**
     * 阅读时间
     */
    private Date readTime;
    private String readTimeLabel;

    private String createTimeLabel;
}
