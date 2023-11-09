package ir.neshan.urlshortener.configuration.security;

import ir.neshan.urlshortener.configuration.security.jwt.JwtAuthenticationConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration {

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity security) throws Exception {

        return security
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(configurer -> configurer.jwt(jwtConfigurer -> jwtConfigurer
                        .jwtAuthenticationConverter(new JwtAuthenticationConverter())))
                .build();
    }
}
