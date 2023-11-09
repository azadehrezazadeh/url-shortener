package ir.neshan.urlshortener;

import ir.neshan.urlshortener.domain.*;
import ir.neshan.urlshortener.mapper.*;
import ir.neshan.urlshortener.repository.*;
import ir.neshan.urlshortener.service.*;
import ir.neshan.urlshortener.utility.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.view.RedirectView;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkRepository repository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private LinkMapper mapper;

    @Mock
    private DateTimeUtility dateTimeUtility;

    private LinkService linkService;

    private final Long ID = 1L;
    private final UUID USER_ID = UUID.fromString("e32a3ac1-769b-44d0-a820-8ebb09899d1a");
    private final String EMAIL = "test@google.com";
    private final ZonedDateTime now = ZonedDateTime.now();

    @BeforeEach
    void setUp() {
        this.linkService = new LinkService(repository, keycloakService, mapper,dateTimeUtility);
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    public void clear() {
        TransactionSynchronizationManager.clear();
    }

    @Test
    @DisplayName("test redirect when shortUrl has parameter")
    void testRedirect() {

        String longUrl ="www.google.com?id=1";
        String redirectedLongUrl ="www.google.com?id=1&name=a";
        String shortUrl = "http://shr.ir/MjRBNDU?name=a";
        String savedShortUrl = "http://shr.ir/MjRBNDU";

        Link foundLink=new Link(ID,USER_ID,longUrl,savedShortUrl,0,now);
        Link updatedLink=new Link(ID,USER_ID,longUrl,savedShortUrl,1,now);

        Mockito.when(repository.findByUserIdAndShortUrl(USER_ID,savedShortUrl))
                .thenReturn(Optional.of(foundLink));

        Mockito.when(keycloakService.getUserId(EMAIL))
                .thenReturn(USER_ID);

        Mockito.when(dateTimeUtility.getZoneDateTime())
                .thenReturn(now);

        Mockito.when(repository.save(updatedLink))
                .thenReturn(updatedLink);

        RedirectView actual = linkService.redirect(EMAIL,shortUrl);


        Assertions.assertNotNull(actual);
        Assertions.assertEquals(redirectedLongUrl, actual.getUrl());

    }


}
