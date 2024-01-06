package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.example.vchatserver.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocketIoAuthorizationInterceptor implements AuthorizationListener {

    @Autowired
    AuthService authService;

    @Override
    public boolean isAuthorized(HandshakeData data) {
        return authService.authenticateByHandshakeData(data);
    }
}
