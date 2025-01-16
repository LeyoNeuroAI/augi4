package tech.intellibio.augi4.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import tech.intellibio.augi4.security.AdminUserDetailsService;
import tech.intellibio.augi4.security.UserRoles;


@Configuration
public class AdminSecurityConfig {

    @Bean
    public AuthenticationManager adminAuthenticationManager(final PasswordEncoder passwordEncoder,
            final AdminUserDetailsService adminUserDetailsService,
            @Value("${admin.rememberMeKey}") final String rememberMeKey) {
        final DaoAuthenticationProvider adminAuthenticationManager = new DaoAuthenticationProvider(passwordEncoder);
        adminAuthenticationManager.setUserDetailsService(adminUserDetailsService);
        return new ProviderManager(adminAuthenticationManager, new RememberMeAuthenticationProvider(rememberMeKey));
    }

    @Bean
    @Order(30)
    public SecurityFilterChain adminFilterChain(final HttpSecurity http,
            @Qualifier("adminAuthenticationManager") final AuthenticationManager adminAuthenticationManager,
            @Value("${admin.rememberMeKey}") final String rememberMeKey,
            final AdminUserDetailsService adminUserDetailsService) throws Exception {
        return http.cors(withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/actuator/**", "/assistant/**"))
                .authorizeHttpRequests(authorize -> authorize
                        
                    .requestMatchers("/error",  "/admin/login", "/admin/register", "/assistant/**",  "/avatars/**", "/css/**", "/dist/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                    .anyRequest().hasAuthority(UserRoles.ADMIN))
                .authenticationManager(adminAuthenticationManager)
                .formLogin(form -> form
                    .loginPage("/admin/login")
                    .usernameParameter("email")
                    .failureUrl("/admin/login?loginError=true")
                        .defaultSuccessUrl("/index", true)
                   )
                .rememberMe(rememberMe -> rememberMe
                    .tokenValiditySeconds(((int)Duration.ofDays(180).getSeconds()))
                    .rememberMeParameter("rememberMe")
                    .rememberMeCookieName("admin-remember-me")
                    .key(rememberMeKey)
                    .userDetailsService(adminUserDetailsService))
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/admin/login?loginRequired=true")))
                .build();
    }

}
