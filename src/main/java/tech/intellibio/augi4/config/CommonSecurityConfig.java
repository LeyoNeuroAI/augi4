package tech.intellibio.augi4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class CommonSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // creates hashes with {bcrypt} prefix
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManager noopAuthenticationManager() {
        return new ProviderManager(new AnonymousAuthenticationProvider("noop"));
    }

    @Bean
    @Order(10)
    public SecurityFilterChain logoutFilterChain(final HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/professional/logout")
                .logout(logout -> logout
                    .logoutUrl("/professional/logout")
                    .logoutSuccessUrl("/professional/login?logoutSuccess=true")
                    .deleteCookies("SESSION"))
                .build();
    }

}
