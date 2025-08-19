package com.varsityvive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {//    throws
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // ✅ Allow CORS preflight
                .requestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")).permitAll()

                // ✅ Allow uploads (POST and any nested paths)
                .requestMatchers(new AntPathRequestMatcher("/api/uploads", "POST")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/uploads/**")).permitAll()

                // Public endpoints your UI calls without auth (keep these)
                .requestMatchers(
                    new AntPathRequestMatcher("/api/auth/**"),
                    new AntPathRequestMatcher("/api/users/**"),
                    new AntPathRequestMatcher("/api/messages/**"),
                    new AntPathRequestMatcher("/api/posts/**"),
                    new AntPathRequestMatcher("/api/clubs/**"),
                    new AntPathRequestMatcher("/api/tournaments/**"),
                    new AntPathRequestMatcher("/uploads/**"),
                    new AntPathRequestMatcher("/index.html"),
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/ws-chat/**"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/favicon.ico"),
                    new AntPathRequestMatcher("/error"),
                    new AntPathRequestMatcher("/static/**"),
                    new AntPathRequestMatcher("/assets/**")
                ).permitAll()

                .anyRequest().authenticated()
            )
            // H2 console & iframes
            .headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Explicit dev origins (older Spring often doesn't need addAllowedOriginPattern)
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:5000");
        config.addAllowedOrigin("http://127.0.0.1:5500"); // VS Code Live Server
        config.addAllowedOrigin("http://127.0.0.1:5173"); // Vite (if you use it)
        config.addAllowedOrigin("null");                  // when opening HTML via file://

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
