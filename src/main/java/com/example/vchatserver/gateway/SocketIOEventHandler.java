package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.handler.SocketIOException;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.example.vchatserver.auth.Auth;
import com.example.vchatserver.auth.AuthService;
import com.example.vchatserver.group.GroupService;
import com.example.vchatserver.message.Message;
import com.example.vchatserver.message.MessageService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocketIOEventHandler {
    @Autowired
    AuthService authService;

    @Autowired
    public void configure(SocketIOServer server) {
        server.addConnectListener(client -> {
            // Получите авторизованного пользователя из Spring Security и добавьте его в список сессий
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication);
            SessionManager.addSession(userId, client);
        });

        server.addDisconnectListener(client -> {
            authService.authenticateByHandshakeData(client.getHandshakeData());
            // Получите авторизованного пользователя из Spring Security и удалите его из списка сессий
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication);
            SessionManager.removeSession(userId);
        });
        server.start();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return Auth.getUser(authentication).getId();
    }
}