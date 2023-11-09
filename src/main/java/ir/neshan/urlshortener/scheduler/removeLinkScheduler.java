package ir.neshan.urlshortener.scheduler;

import ir.neshan.urlshortener.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
@RequiredArgsConstructor
public class removeLinkScheduler {

    private final LinkService linkService;
    private final ReentrantLock mutex = new ReentrantLock();

    @Value("${scheduler.enable}")
    private Boolean isEnable;

    @Scheduled(cron = "${scheduler.expression}")
    // @Scheduled(fixedDelayString = "${scheduler.fixed-delay}", initialDelayString = "${scheduler.initial-delay}")
    public void runReInitializeScheduler() {

        if (isEnable == null || !isEnable)
            return;

        log.info("Try to run RemoveLink scheduler");

        if (mutex.isLocked()) {
            log.warn("A thread running RemoveLink Scheduler in the same time");
            return;
        }

        try {
            mutex.lock();

            log.info("Start RemoveLink scheduling");
            linkService.removeLinkScheduler();
            log.info("Exit RemoveLink scheduling");
        } catch (Exception e) {
            log.error("There is an error in RemoveLink schedule");
            log.error("Error message is: {}", e);
        } finally {
            mutex.unlock();
        }
    }
}

