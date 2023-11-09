package ir.neshan.urlshortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public UserRepresentation getUser(String email) {
        return keycloak.realm(realm)
                .users()
                .searchByEmail(email, true)
                .stream()
                .findAny()
                .orElseThrow();
    }

    public UserRepresentation getUserById(UUID id) {
        return keycloak.realm(realm)
                .users()
                .get(id.toString())
                .toRepresentation();

    }

    public UUID getUserId(String emailAddress) {
        var user = getUser(emailAddress);
        log.info(user.getId());
        return UUID.fromString(user.getId());

    }

    public Integer getUrlCount(String emailAddress) {

        var user = getUser(emailAddress);
        if (user.getAttributes()== null ||!user.getAttributes().containsKey("url-count") )
            return 0;
        else
            return Integer.parseInt(user.getAttributes().get("url-count").get(0));

    }

    public void increaseUrlCount(String emailAddress) {
        var user = getUser(emailAddress);
        String attributeName = "url-count";

        if (user.getAttributes()== null )
           user.setAttributes(new HashMap<>());

        if (!user.getAttributes().containsKey(attributeName) )
           user.getAttributes().put(attributeName,List.of("1"));
        else
        {
            Integer newValue=Integer.parseInt(user.getAttributes().get(attributeName).get(0))+1;
            user.getAttributes().put(attributeName,List.of(newValue.toString()));
        }

        keycloak.realm(realm)
                .users()
                .get(user.getId())
                .update(user);

        log.debug("After increaseUrlCount userId:{} ,urlCount:{}", user.getId(), user.getAttributes().get("url-count").get(0));

    }

    public void decreaseUrlCount(UUID userId, Integer removedCount) {
        var user = getUserById(userId);
        String attributeName = "url-count";

        if (user.getAttributes() != null && user.getAttributes().containsKey(attributeName) ) {
            Integer newValue = Integer.parseInt(user.getAttributes().get(attributeName).get(0)) - removedCount;
            newValue = newValue < 0 ? 0 : newValue;
            user.getAttributes().put(attributeName, List.of(newValue.toString()));

            keycloak.realm(realm)
                    .users()
                    .get(user.getId())
                    .update(user);

            log.debug("After decreaseUrlCount userId:{} ,urlCount:{}", user.getId(), user.getAttributes().get("url-count").get(0));
        }
    }
}
