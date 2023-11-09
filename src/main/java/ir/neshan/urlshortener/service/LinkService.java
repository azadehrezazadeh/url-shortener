package ir.neshan.urlshortener.service;

import ir.neshan.urlshortener.domain.Link;
import ir.neshan.urlshortener.dto.*;
import ir.neshan.urlshortener.exception.*;
import ir.neshan.urlshortener.exception.APIException.*;
import ir.neshan.urlshortener.mapper.LinkMapper;
import ir.neshan.urlshortener.repository.LinkRepository;
import ir.neshan.urlshortener.utility.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository repository;
    private final KeycloakService keycloakService;
    private final LinkMapper mapper;
    private final DateTimeUtility dateTimeUtility;
    private static final Integer MAX_URL_COUNT = 2;
    private static final String DOMAIN = "http://shr.ir/";

    ////////////////////////
    // LongUrl to ShortUrl
    ////////////////////////
    public LinkDTO createLink(String emailAddress, CreateLinkDTO createLinkDTO) {

        log.info("createLink email:{},longUrl:{}", emailAddress, createLinkDTO.getLongUrl());

        checkMaxUrlCount(emailAddress);

        UUID userId = keycloakService.getUserId(emailAddress);

        Link link = createShortUrl(createLinkDTO, userId);

        keycloakService.increaseUrlCount(emailAddress);

        log.info("createLink successfully done!");

        return mapper.toDTO(link);

    }

    private Link createShortUrl(CreateLinkDTO createLinkDTO, UUID userId) {

        Link link = mapper.fromCreateDTO(createLinkDTO, userId);
        String normalLongUrl = normalize(link.getLongUrl().trim());

        if (repository.existsByLongUrlAndUserId(normalLongUrl,userId))
            throw new APIException(Code.DUPLICATED, "url is duplicated!");

        link.setLongUrl(normalLongUrl);
        link.setShortUrl(calculateShortLink(normalLongUrl, userId));
        link.setLastVisit(dateTimeUtility.getZoneDateTime());

        link = repository.save(link);

        log.debug("saved link :{}", link);
        return link;
    }

    private void checkMaxUrlCount(String emailAddress) {
        Integer urlCount = keycloakService.getUrlCount(emailAddress);

        log.debug("checkMaxUrlCount email:{},urlCount:{}", emailAddress, urlCount);

        if (urlCount == MAX_URL_COUNT)
            throw new APIException(Code.ACCESS_DENIED, "max url count is exceeded!");
    }

    private String calculateShortLink(String longUrl, UUID userId) {
        String url = longUrl + userId.toString() + dateTimeUtility.getZoneDateTime();
        String md5Hex = DigestUtils.md5Hex(url).toUpperCase();
        String shortLink = DOMAIN + Base64Coder.encodeString(md5Hex).substring(0, 7);

        log.debug("calculateShortLink userId:{},shortLink:{}", userId, shortLink);
        return shortLink;
    }

    private String normalize(String url) {
        log.debug("before normalizeUrl url:{}", url);
        if (url.substring(0, 7).equals("http://"))
            url = url.substring(7);

        if (url.substring(0, 8).equals("https://"))
            url = url.substring(8);

        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);

        log.debug("after normalizeUrl url:{}", url);
        return url;
    }

    //////////////////////
    // ShortUrl to LongUrl
    //////////////////////
    public RedirectView redirect(String emailAddress, String shortUrl) {
        log.info("redirect email:{} , shortUrl:{}", emailAddress, shortUrl);

        ExtractedLink extractedLink = extractParameters(shortUrl.trim());
        Link link = getLinkByShortUrl(emailAddress, extractedLink.shortUrl);
        link = visitLink(link);
        String longUrl = makeLongUrl(link, extractedLink.parameters);
        RedirectView redirectView = makeRedirectView(longUrl);

        log.info("redirect to {}", longUrl);
        return redirectView;
    }

    private Link visitLink(Link link) {
        link.setLastVisit(dateTimeUtility.getZoneDateTime());
        link.setVisitCount(link.getVisitCount() + 1);
        log.debug("visitLink:{}", link);
        return repository.save(link);
    }

    private RedirectView makeRedirectView(String longUrl) {
        RedirectView redirectView = new RedirectView();

        redirectView.setStatusCode(HttpStatus.FOUND);
        redirectView.setUrl(longUrl);

        return redirectView;
    }

    private String makeLongUrl(Link link, String parameters) {
        String longUrl;

        if (parameters.isEmpty())
            return link.getLongUrl();

        if (link.getLongUrl().contains("?"))
            longUrl = link.getLongUrl() + "&" + parameters;
        else
            longUrl = link.getLongUrl() + "?" + parameters;
        log.debug("longUrl:{} after attach parameters", longUrl);
        return longUrl;
    }

    private ExtractedLink extractParameters(String shortUrl) {
        ExtractedLink extractedLink;
        if (shortUrl.contains("?"))
            extractedLink = new ExtractedLink(shortUrl.substring(0, shortUrl.indexOf('?')), shortUrl.substring(shortUrl.indexOf('?') + 1));
        else
            extractedLink = new ExtractedLink(shortUrl, "");

        log.debug("shortUrl :{} parameters:{}", extractedLink.shortUrl, extractedLink.parameters);
        return extractedLink;
    }

    private record ExtractedLink(String shortUrl, String parameters) {}

    /////////////////////
    // Common
    /////////////////////
    public List<LinkDTO> getAll(String emailAddress) {

        return repository.findAllByUserId(keycloakService.getUserId(emailAddress))
                .stream().map(link -> mapper.toDTO(link)).collect(Collectors.toList());

    }

    public Integer getVisitCount(String emailAddress, String longUrl) {
        return getLinkByLongUrl(emailAddress, longUrl)
                .getVisitCount();
    }

    private Link getLinkByLongUrl(String emailAddress, String longUrl) {
        return repository.findByUserIdAndLongUrl(keycloakService.getUserId(emailAddress), longUrl.trim())
                .or(() -> {
                    throw new APIException(Code.NOT_FOUND, "invalid url");
                })
                .get();
    }

    private Link getLinkByShortUrl(String emailAddress, String shortUrl) {
        return repository.findByUserIdAndShortUrl(keycloakService.getUserId(emailAddress), shortUrl.trim())
                .or(() -> {
                    throw new APIException(Code.NOT_FOUND, "invalid url");
                })
                .get();
    }

    public void deleteLink(String emailAddress,Long id) {
        log.info("deleteLink id:{} by email:{} ",id,emailAddress);

        UUID userId = keycloakService.getUserId(emailAddress);

        if (!repository.existsByIdAndUserId(id,userId))
            throw new APIException(Code.NOT_FOUND, "invalid linkId");

        repository.deleteById(id);

        keycloakService.decreaseUrlCount(userId,1);

        log.info("link with id:{} is deleted",id);
    }

    /////////////////
    // Scheduler
    /////////////////
    @Transactional
    public void removeLinkScheduler() {

        List<Link> unvisitedLinks = repository.findAllByLastVisit(dateTimeUtility.getZoneDateTime().minusYears(1));

        decreaseUrlCountOfUsers(unvisitedLinks);

        repository.deleteAll(unvisitedLinks);
    }

    private void decreaseUrlCountOfUsers(List<Link> unvisitedLinks) {
        log.debug("decreaseUrlCountOfUsers :{}", unvisitedLinks);

        /*
        calculate removedLinkCount per userId
        */
        Map<UUID, Integer> removeCountPerUserId = new HashMap<>();
        unvisitedLinks.forEach(link -> {
            if (removeCountPerUserId.containsKey(link.getUserId()))
                removeCountPerUserId.put(link.getUserId(), removeCountPerUserId.get(link.getUserId()) + 1);
            else
                removeCountPerUserId.put(link.getUserId(), 1);
        });

        /*
        update keyCloak (decrease urlCount)
        */
        removeCountPerUserId.forEach((userId, removedCount) ->
            keycloakService.decreaseUrlCount(userId, removedCount));

        log.debug("keyCloak user infos are updated");
    }
}
