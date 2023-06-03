package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {
    @Autowired
    SocketIoAuthorizationInterceptor socketIoAuthorizationInterceptor;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setPort(4000);
        config.setAuthorizationListener(socketIoAuthorizationInterceptor);
        return new SocketIOServer(config);
    }
}
