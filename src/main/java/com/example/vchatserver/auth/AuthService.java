package com.example.vchatserver.auth;

import com.corundumstudio.socketio.HandshakeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;

@Component
public class AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    public boolean authenticateByHandshakeData(HandshakeData data) {
        String authorization = new ArrayList<>(data.getUrlParams().keySet()).get(0);
        if (authorization != null && authorization.startsWith("Basic ")) {
            String[] credentials = new String(Base64.getDecoder().decode(
                    authorization.substring("Basic ".length()))).split(":");
            String username = credentials[0].toLowerCase();
            String password = credentials[1];

            CustomUserDetails user = customUserDetailsService.loadUserByUsername(username);
            if (user != null) {
                if (passwordEncoder.matches(password, user.getUser().getPassword())) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return true;
                }
            }
        }
        return false;
    }
}
