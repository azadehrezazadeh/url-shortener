package ir.neshan.urlshortener.configuration;

import lombok.*;
import org.keycloak.*;
import org.keycloak.admin.client.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

@Configuration
@ConfigurationProperties("keycloak.admin-client")
@Setter
public class KeycloakConfiguration {

    private String baseUrl;
    private String realm;
    private String clientId;
    private String username;
    private String password;

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(baseUrl)
                .realm(realm)
                .clientId(clientId)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .build();
    }
}
