package com.multi.laptellect.config.Security;

import com.multi.laptellect.error.CustomAuthenticationFailureHandler;
import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationFailureHandler customFailureHandler;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final RedisUtil redisUtil;

    @Bean // 비밀번호 암호화
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CustomAuthenticationProvider 원본 비밀번호와 임시 비밀번호 동시 사용을 위해 커스텀
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(customUserDetailsService, bCryptPasswordEncoder(), redisUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean // static 예외 처리
    public WebSecurityCustomizer configure() {

        return (web) -> web.ignoring().requestMatchers(
                "/css/**", "/js/**", "/img/**"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/customer/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                );
        http
                .formLogin((auth) -> auth.loginPage("/signin")
                        .loginProcessingUrl("/signin").permitAll()
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customFailureHandler));
        http
                .rememberMe() // 자동 로그인
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(604800) // 14일
                .alwaysRemember(false) // 항상 실행 false
                .userDetailsService(customUserDetailsService);
        http
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
                        .deleteCookies("remember-me")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/"));

        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/signin")
                        .sessionRegistry(sessionRegistry()));

        http
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedPage("/error/denied"));

        http
                .csrf((auth) -> auth.disable());


        return http.build();
    }

}