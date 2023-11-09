package ir.neshan.urlshortener.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
@Slf4j
public class DateTimeUtility {

    private static final ZoneId IRR_ZONE_ID = ZoneId.of("Asia/Tehran");

    public LocalTime getJalaliLocalTime() {

        LocalTime now = LocalTime.now(IRR_ZONE_ID);
        log.debug("Time of IRR zone is: {}", now);
        return now;
    }

    public LocalDate getJalaliLocalDate() {

        LocalDate now = LocalDate.now(IRR_ZONE_ID);
        log.debug("Date of IRR zone is: {}", now);
        return now;
    }

    public ZonedDateTime getZoneDateTime() {

        ZonedDateTime now = ZonedDateTime.now();
        log.debug("DateTime of IRR zone is: {}", now);
        return now;
    }
}
