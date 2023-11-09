package ir.neshan.urlshortener.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLinkDTO {
    @NotNull
    private String longUrl;
}
