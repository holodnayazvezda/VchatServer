package com.example.vchatserver.auth;

import com.example.vchatserver.exceptions.UserNotFoundException;
import com.example.vchatserver.user.User;
import com.example.vchatserver.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String nickname) throws UserNotFoundException {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.map(CustomUserDetails::new).orElse(null);
    }
}
