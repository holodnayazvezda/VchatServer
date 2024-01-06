package com.example.vchatserver.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth
                .authenticationProvider(customAuthenticationProvider)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint()))
                .authorizeRequests(requests -> requests
                        .antMatchers("/docs",
                                "/docs/**",
                                "/docs.yaml",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/v1.0/user/create",
                                "/user-controller/create",
                                "/v1.0/user/get_base_info",
                                "/user-controller/getBaseInfo",
                                "/v1.0/user/exists",
                                "/user-controller/exists",
                                "/v1.0/user/change_password_by_secret_keys",
                                "/user-controller/changePasswordBySecretKeys",
                                "/v1.0/user/check_password",
                                "/user-controller/checkPassword",
                                "/v1.0/user/check_secret_keys",
                                "/user-controller/checkSecretKeys",
                                "/v1.0/check_name",
                                "/name-controller/checkName",
                                "/v1.0/check_nickname_for_user",
                                "/nickname-controller/checkNicknameForUser",
                                "/v1.0/check_nickname_for_channel",
                                "/nickname-controller/checkNicknameForChannel",
                                "/v1.0/check_password_conditions",
                                "/password-controller/checkPasswordConditions",
                                "/v1.0/check_password_confirmation",
                                "/password-controller/checkPasswordConfirmation",
                                "/v1.0/check_password_all_conditions",
                                "/password-controller/checkPasswordAllConditions")
                        .permitAll()
                        .anyRequest().authenticated());
        return httpSecurity.build();
    }
}