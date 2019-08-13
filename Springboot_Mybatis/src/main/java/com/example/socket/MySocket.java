package com.example.socket;

import com.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;


@Component
@ServerEndpoint(value = "/ws/nvwaSocket",configurator = HttpSessionConfigurator.class)
public class MySocket {

    private Logger log = LoggerFactory.getLogger(MySocket.class);


    private Session session;

    //J.U.C包下线程安全的类，主要用来存放每个客户端对应的webSocket连接
    private static CopyOnWriteArraySet<MySocket> copyOnWriteArraySet = new CopyOnWriteArraySet<MySocket>();

        /**
         * 打开连接。进入页面后会自动发请求到此进行连接_ 将用户信息写入session中
         * @param session
         */
        @OnOpen
        public void onOpen(Session session, EndpointConfig config) {
            HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            User user= (User) config.getUserProperties().get(User.class.getName());
            if(httpSession.getId()!=null){
                session.getUserProperties().put("httpSessionId",httpSession.getId());
                session.getUserProperties().put("user",user);
            }
            this.session = session;
            copyOnWriteArraySet.add(this);
            log.info("websocket有新的连接, 总数:"+ copyOnWriteArraySet.size());

        }


        @OnClose
        public void onClose() {
            copyOnWriteArraySet.remove(this);
            log.info("websocket连接断开, 总数:"+ copyOnWriteArraySet.size());
        }

        @OnMessage
        public void onMessage(String message) {
            log.info("websocket收到客户端发来的消息:"+message);
        }



        @OnError
        public void onError(Session session, Throwable error) {
            log.error("发生错误：" + error.getMessage(), session.getId());
            error.printStackTrace();
        }


        public void sendMessage(String message) {
            //遍历客户端
            for (MySocket webSocket : copyOnWriteArraySet) {
                log.info("websocket广播消息：" + message);
                try {
                    //服务器主动推送
                    webSocket.session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        public void sendMessage(String sessionId,String message) throws Exception {
            Session session = null;
            MySocket tempWebSocket = null;
            for (MySocket webSocket : copyOnWriteArraySet) {
                String httpSessionId = (String) webSocket.session.getUserProperties().get("httpSessionId");
                if (httpSessionId.equals(sessionId)) {
                    tempWebSocket = webSocket;
                    session = webSocket.session;
                    break;
                }
            }
            if (session != null) {
                tempWebSocket.session.getBasicRemote().sendText(message);
//                tempWebSocket.session.getBasicRemote().sendBinary(map);
            } else {
                log.warn("没有找到你指定ID的会话：{}", sessionId);
            }
        }

        public void sendMessage(String message,long userId) throws Exception{
            Session session = null;
            MySocket tempWebSocket = null;
            for (MySocket webSocket : copyOnWriteArraySet) {
                User user= (User) webSocket.session.getUserProperties().get("user");
                if(user==null){
                    continue;
                }
                if (user.getId()==userId) {
                    tempWebSocket = webSocket;
                    session = webSocket.session;
                    break;
                }
            }
            if (session != null) {
                tempWebSocket.session.getBasicRemote().sendText(message);
            } else {
                log.warn("当前用户没有登录创建会话：{}", userId);
            }

        }

    }

