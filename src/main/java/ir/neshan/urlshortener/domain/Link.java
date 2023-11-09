package ir.neshan.urlshortener.domain;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="link" ,indexes = {
        @Index(name = "long_url_index", columnList = "long_url"),
        @Index(name = "short_url_index", columnList = "short_url"),
        @Index(name = "last_visit_index", columnList = "last_visit")
})
public class Link {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Size(min = 1,max =200)
    @Column(name = "long_url", length = 200, nullable = false)
    private String longUrl;

    @NotNull
    @Size(min = 1,max =50)
    @Column(name = "short_url", length = 50, nullable = false)
    private String shortUrl;

    @NotNull
    @Column(name = "visit_count", nullable = false)
    @Min(value = 0)
    private Integer visitCount;

    @NotNull
    @Column(name = "last_visit", nullable = false)
    private ZonedDateTime lastVisit;
}
