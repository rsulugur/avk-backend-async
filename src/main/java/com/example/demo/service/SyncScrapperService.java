package com.example.demo.service;

import com.example.demo.model.AuditLog;
import com.example.demo.model.Product;
import com.example.demo.model.Scrapper;
import com.example.demo.repository.AuditRepository;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class SyncScrapperService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncScrapperService.class);
    private final OpenAIService openAIService;
    private final AuditRepository auditRepository;
    private final AmazonScrapper amazonScrapper;
    private final EbayScrapper ebayScrapper;

    public Flux<Product> scrapProductDetails(final String productName) {
        List<Scrapper> availableScrappers = List.of(amazonScrapper, ebayScrapper);
        AtomicInteger totalCount = new AtomicInteger(0);


        return Flux.fromIterable(availableScrappers)
                .flatMap(scrapper -> scrapper.crawl(productName))
                .doOnNext(product -> totalCount.incrementAndGet());
//                .doOnComplete(() -> handleAudit(startTime, productName, totalCount.get()));
    }

    public Flux<Product> queryProductDetails(final String userDescription) {
        List<Scrapper> availableScrappers = List.of(amazonScrapper, ebayScrapper);
        final AuditLog auditLog = new AuditLog();
        final long startTime = System.currentTimeMillis();
        auditLog.setUserQuery(userDescription);
        return openAIService
                .queryFeatures(userDescription)
                .doOnNext(product -> auditLog.getSuggestedProducts().add(product))
                .flatMap(productName -> Flux.fromIterable(availableScrappers).flatMap(scrapper -> scrapper.crawl(productName)))
                .doOnNext(product -> auditLog.incrementRecords())
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    final long endTime = System.currentTimeMillis();
                    final long totalTime = endTime - startTime;
                    auditLog.setTimeTaken(totalTime / 1000.0);
                    auditLog.setCreatedDate(LocalDateTime.now());
                    auditRepository.save(auditLog);
                });
    }

//    private void handleAudit(Long startTime, String searchKey, Integer totalCount) {
//        LOGGER.debug("Service: Persisting Audit Record in DB Started");
//        final double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
//        auditRepository.save(
//                AuditLog.builder()
//                        .timeTaken(timeTaken)
//                        .totalCount(totalCount)
//                        .searchQuery(searchKey)
//                        .createdDate(LocalDateTime.now())
//                        .build()
//        );
//        LOGGER.debug("Service: Persisting Audit Record Completed");
//    }

}
