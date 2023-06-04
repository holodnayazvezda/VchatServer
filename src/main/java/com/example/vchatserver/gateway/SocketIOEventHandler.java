package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.vchatserver.auth.Auth;
import com.example.vchatserver.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SocketIOEventHandler {
    @Autowired
    AuthService authService;

    @Autowired
    public void configure(SocketIOServer server) {
        server.addConnectListener(client -> {
            // Получить авторизованного пользователя из Spring Security и добавьте его в список сессий
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId != null) {
                SessionManager.addSession(userId, client);
            }
        });

        server.addDisconnectListener(client -> {
            authService.authenticateByHandshakeData(client.getHandshakeData());
            // Получить авторизованного пользователя из Spring Security и удалите его из списка сессий
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId != null) {
                SessionManager.removeSession(userId);
            }
        });
        server.start();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        try {
            return Auth.getUser(authentication).getId();
        } catch (NullPointerException e) {
            return null;
        }
    }
}