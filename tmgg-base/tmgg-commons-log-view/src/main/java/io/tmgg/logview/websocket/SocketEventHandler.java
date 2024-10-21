package io.tmgg.logview.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import cn.moon.logview.TailFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class SocketEventHandler extends AbstractWebSocketHandler {

    public static final int INTERVAL = 500;

    private final Map<String, TailFile> tailFileMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("打开连接 {} {}", session.getId(), session.getUri());

        String query = session.getUri().getQuery();
        String path = query.substring(query.indexOf("=") + 1);

        path = URLDecoder.decode(path);

        sendMessageTo(session, path);

        File file = new File(path);
        if (!file.exists()) {
            sendMessageTo(session, "对不起，文件不存在：" + file.getAbsolutePath());
            return;
        }

        // 只读取合法路径，防止有心人利用查看敏感文件
        if(!StringUtils.endsWithIgnoreCase(path,".log") && !StringUtils.endsWithIgnoreCase(path,".txt")){
            sendMessageTo(session,"只能读取.log,.txt文件");
            return;
        }


        TailFile tailFile = new TailFile(file, INTERVAL, list -> {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(s).append("\r\n");
            }
            sendMessageTo(session, sb.toString());
        });
        tailFile.start();

        tailFileMap.put(session.getId(), tailFile);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("关闭连接 {} {}", session.getId(), status);
        stopTailFile(session);
    }


    private void sendMessageTo(WebSocketSession session, String message) {
        synchronized (session.getId()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                } else {
                    stopTailFile(session);
                }
            } catch (Exception e) {
                e.printStackTrace();
                stopTailFile(session);
            }
        }
    }

    private void stopTailFile(WebSocketSession session) {
        TailFile tailFile = tailFileMap.get(session.getId());
        if (tailFile != null) {
            tailFile.stopRunning();
        }

        tailFileMap.remove(session.getId());
    }
}
