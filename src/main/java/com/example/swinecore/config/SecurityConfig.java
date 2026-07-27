package com.example.swinecore.config;

import com.example.swinecore.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/api/factory/**")
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**",
                                  "/uploads/**", "/webjars/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/customer/register").permitAll()
                .requestMatchers("/marketplace").permitAll()
                .requestMatchers("/api/factory/**").permitAll()
                // The HMAC-signed QR is the voucher credential on the scanning device.
                .requestMatchers("/checkout/**").permitAll()
                // Admin only
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Manager
                .requestMatchers("/manager/**").hasRole("MANAGER")
                // HR
                .requestMatchers("/hr/**").hasRole("HR")
                // Supervisor
                .requestMatchers("/supervisor/**").hasRole("SUPERVISOR")
                // Staff
                .requestMatchers("/staff/**").hasRole("STAFF")
                // Customer portal (dashboard, pigs, semen, orders, profile)
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                // Shared authenticated endpoints
                .requestMatchers("/profile/**", "/api/attendance/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureHandler((request, response, exception) -> {
                    String message = exception.getMessage();
                    if (message != null && (message.startsWith("SUSPENDED|") || message.startsWith("BANNED|"))) {
                        String[] parts = message.split("\\|", 2);
                        response.sendRedirect("/auth/login?accountStatus=" + parts[0] + "&reason=" +
                            URLEncoder.encode(parts.length > 1 ? parts[1] : "Contact support", StandardCharsets.UTF_8));
                    } else response.sendRedirect("/auth/login?error=true");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/auth/login?expired=true")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }
}
