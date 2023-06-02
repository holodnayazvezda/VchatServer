package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.example.vchatserver.auth.AuthService;
import com.example.vchatserver.auth.CustomUserDetails;
import com.example.vchatserver.auth.CustomUserDetailsService;
import com.example.vchatserver.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;

@Component
public class SocketIoAuthorizationInterceptor implements AuthorizationListener {

    @Autowired
    AuthService authService;

    @Override
    public boolean isAuthorized(HandshakeData data) {
        return authService.authenticateByHandshakeData(data);
    }
}
