package ir.neshan.urlshortener.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class FetchLinkDTO {
    @NotNull
    private String url;
}
