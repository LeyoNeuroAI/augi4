package tech.intellibio.augi4.config;

import java.nio.file.AccessDeniedException;

import java.time.Duration;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tech.intellibio.augi4.security.ProfessionalUserDetailsService;
import tech.intellibio.augi4.security.UserRoles;


@Configuration
public class ProfessionalSecurityConfig {

    @Bean
    public AuthenticationManager professionalAuthenticationManager(
            final PasswordEncoder passwordEncoder,
            final ProfessionalUserDetailsService professionalUserDetailsService,
            @Value("${professional.rememberMeKey}") final String rememberMeKey) {
        final DaoAuthenticationProvider professionalAuthenticationManager = new DaoAuthenticationProvider(passwordEncoder);
        professionalAuthenticationManager.setUserDetailsService(professionalUserDetailsService);
        return new ProviderManager(professionalAuthenticationManager, new RememberMeAuthenticationProvider(rememberMeKey));
    }

    @Bean
    @Order(20)
    public SecurityFilterChain professionalFilterChain(final HttpSecurity http,
            @Qualifier("professionalAuthenticationManager") final AuthenticationManager professionalAuthenticationManager,
            @Value("${professional.rememberMeKey}") final String rememberMeKey,
            final ProfessionalUserDetailsService professionalUserDetailsService) throws Exception {
       return http.securityMatcher("/feedbacks/**", "/professional/**")
                       .cors(cors -> cors.configurationSource(corsConfigurationSource()))

      
                .csrf(withDefaults())
        .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/professional/register",  "/professional/terms", "/professional/login", "/logout")
                .permitAll()
                .requestMatchers("/countries", "GET")
                .hasAuthority(UserRoles.PROFESSIONAL)
                .requestMatchers("/professional/**")
                .authenticated()
                .anyRequest()
                .permitAll())

                .authenticationManager(professionalAuthenticationManager)
                .formLogin(form -> form
                    .loginPage("/professional/login")
                    .usernameParameter("email")
                    .failureUrl("/professional/login?loginError=true")
                .defaultSuccessUrl("/professional/index", true))
                  
             
                    
                .rememberMe(rememberMe -> rememberMe
                    .tokenValiditySeconds(((int)Duration.ofDays(180).getSeconds()))
                    .rememberMeParameter("rememberMe")
                    .rememberMeCookieName("professional-remember-me")
                    .key(rememberMeKey)
                    .userDetailsService(professionalUserDetailsService))
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/professional/login?loginRequired=true")))
                .build();
    }
    
    
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "https://beta.intellibio.tech"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @RestControllerAdvice
    public static class GlobalExceptionHandler {
        
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleStreamError(Exception ex) {
            return ResponseEntity.internalServerError()
                               .body("Error: " + ex.getMessage());
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                               .body("Access denied: " + ex.getMessage());
        }
    }
}


