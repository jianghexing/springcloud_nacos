package com.example.socket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

public class HttpSessionConfigurator extends Configurator {

     @Override
     public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
         //注释部分是 如果使用shrio 获取用户信息
//              Subject subject = SecurityUtils.getSubject();
//              User user = (User)subject.getPrincipal();
//              if (user == null) {
//                  return;
//               }
               HttpSession httpSession = (HttpSession)request.getHttpSession();
//               sec.getUserProperties().put(User.class.getName(),user);
               sec.getUserProperties().put(HttpSession.class.getName(),httpSession);

    }
}
