package ir.neshan.urlshortener.configuration.security.jwt;

import org.springframework.core.convert.converter.*;
import org.springframework.security.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;

import java.util.*;

public class JwtAuthenticationConverter implements Converter<Jwt, AuthenticationToken> {

    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public final AuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        String principalClaimValue = jwt.getClaimAsString(JwtClaimNames.SUB);
        return new AuthenticationToken(jwt, authorities, principalClaimValue);
    }
}
