package ir.neshan.urlshortener.repository;

import ir.neshan.urlshortener.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findAllByUserId(UUID userId);

    @Query("select l from Link l where l.userId = :userId and trim(l.longUrl)= :longUrl")
    Optional<Link> findByUserIdAndLongUrl(UUID userId, String longUrl);

    @Query("select l from Link l where l.userId = :userId and trim(l.shortUrl)= :shortUrl")
    Optional<Link> findByUserIdAndShortUrl(UUID userId, String shortUrl);

    @Query("select l from Link l where l.lastVisit < :time")
    List<Link> findAllByLastVisit(ZonedDateTime time);

    boolean existsByLongUrlAndUserId(String normalLongUrl, UUID userId);

    boolean existsByIdAndUserId(Long id, UUID userId);
}
