package ir.neshan.urlshortener.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class LinkDTO {
    private Long id;
    private String longUrl;
    private String shortUrl;
    private Integer visitCount;
    private ZonedDateTime lastVisit;
}
