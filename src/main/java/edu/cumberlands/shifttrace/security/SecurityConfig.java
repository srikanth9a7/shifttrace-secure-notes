package edu.cumberlands.shifttrace.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService demoAccounts(
            @Value("${demo.analyst.password}") String analystPassword,
            @Value("${demo.admin.password}") String adminPassword,
            PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("analyst")
                    .password(encoder.encode(analystPassword))
                    .roles("ANALYST").build(),
                User.withUsername("admin")
                    .password(encoder.encode(adminPassword))
                    .roles("ADMIN").build()
        );
    }

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/notes", "/api/notes/search")
                    .hasAnyRole("ANALYST", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/summary").hasRole("ADMIN")
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            // CSRF protection remains enabled by default; this lab is read-only.
            .build();
    }
}
