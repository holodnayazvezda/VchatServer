package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.vchatserver.auth.CustomAuthenticationProvider;
import com.sun.source.tree.Tree;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;

import java.util.*;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {
    @Autowired
    SocketIoAuthorizationInterceptor socketIoAuthorizationInterceptor;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setPort(4000);
        config.setHostname("localhost");
        config.setAuthorizationListener(socketIoAuthorizationInterceptor);
        SocketIOServer server = new SocketIOServer(config);
        return server;
    }
}
