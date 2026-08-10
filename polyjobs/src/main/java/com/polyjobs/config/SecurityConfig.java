package com.polyjobs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                // Tắt CSRF cho WebSocket, API và Like endpoint (AJAX)
                .ignoringRequestMatchers("/ws-chat/**", "/api/**", "/community/like/**")
            )
            .authorizeHttpRequests(auth -> auth
                // ═══ PUBLIC: Ai cũng truy cập được ═══
                .requestMatchers(
                    "/", "/login", "/register",
                    "/jobs", "/jobs/**", "/job/**",
                    "/companies", "/companies/**",
                    "/candidates", "/candidates/**",
                    "/about",
                    "/css/**", "/js/**", "/images/**", "/uploads/**",
                    "/ws-chat/**",
                    "/error"
                ).permitAll()

                // ═══ ADMIN: Chỉ admin ═══
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ═══ EMPLOYER: Chỉ nhà tuyển dụng ═══
                .requestMatchers("/employer/**").hasRole("EMPLOYER")

                // ═══ CÒN LẠI: Phải đăng nhập ═══
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
