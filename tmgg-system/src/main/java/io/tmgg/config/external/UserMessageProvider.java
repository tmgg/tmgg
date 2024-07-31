package io.tmgg.config.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 消息提供者，比如pc端右上角的消息，按分类提供
 */
public interface UserMessageProvider {

    /**
     * 消息分类， 如 系统消息，通知公告，待办信息等
     *
     *
     */
    String userMsgType();

    int count();

    List<UserMessageVo> list();


    @Getter
    @Setter
    class UserMessageVo {
        String title;
        String content;
        String createTime;

        public UserMessageVo() {
        }

        public UserMessageVo(String title, String content, String createTime) {
            this.title = title;
            this.content = content;
            this.createTime = createTime;
        }
    }
}
