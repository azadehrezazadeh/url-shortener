package ir.neshan.urlshortener.controller;

import ir.neshan.urlshortener.configuration.security.principal.*;
import ir.neshan.urlshortener.dto.*;
import ir.neshan.urlshortener.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    @PostMapping("")
    public ResponseEntity<LinkDTO> createLink(@AuthenticationPrincipal Principal principal, @RequestBody CreateLinkDTO createLinkDTO)
    {
        log.info("Try to create short link for :{} by user:{}",createLinkDTO.getLongUrl(),principal.userName());
        return ResponseEntity.ok().body(linkService.createLink(principal.emailAddress(),createLinkDTO));
    }

    @PostMapping("/redirect")
    public RedirectView getLink(@AuthenticationPrincipal Principal principal, @RequestBody FetchLinkDTO fetchLinkDTO) {
        return linkService.redirect(principal.emailAddress(), fetchLinkDTO.getUrl());
    }

    @PostMapping("/visit-count")
    public ResponseEntity<Integer> getVisitCount(@AuthenticationPrincipal Principal principal, @RequestBody FetchLinkDTO fetchLinkDTO) {
        return ResponseEntity.ok()
                    .body(linkService.getVisitCount(principal.emailAddress(), fetchLinkDTO.getUrl()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LinkDTO>> getAllLink(@AuthenticationPrincipal Principal principal)
    {
        return ResponseEntity.ok()
                .body(linkService.getAll(principal.emailAddress()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@AuthenticationPrincipal Principal principal,@PathVariable Long id)
    {
        linkService.deleteLink(principal.emailAddress(),id);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/test-scheduler")
    public ResponseEntity<Void> removeLinkScheduler()
    {
        linkService.removeLinkScheduler();
        return ResponseEntity.noContent()
                .build();
    }
}
