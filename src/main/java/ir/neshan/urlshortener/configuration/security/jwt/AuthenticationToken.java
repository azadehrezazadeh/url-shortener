package ir.neshan.urlshortener.configuration.security.jwt;

import ir.neshan.urlshortener.configuration.security.principal.*;
import org.springframework.security.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;

import java.util.*;
import java.util.function.*;

public class AuthenticationToken extends JwtAuthenticationToken {

    public AuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String name) {
        super(jwt, authorities, name);
    }

    private Principal principal;

    @Override
    public Object getPrincipal() {
        return principal != null ? principal : (principal = createPrincipal());
    }

    private Principal createPrincipal() {
        var configuration = getToken().getClaimAsStringList("configuration");
        return new Principal(
                extract("preferred_username", String::toString),
                extract("email", String::toString));
    }

    private <T> T extract(String claim, Function<String, T> converter) {
        return Optional.ofNullable(getToken().getClaimAsString(claim))
                .map(converter)
                .orElse(null);
    }
}
