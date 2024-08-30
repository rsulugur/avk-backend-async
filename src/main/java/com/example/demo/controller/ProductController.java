package com.example.demo.controller;

import com.example.demo.model.AuditLog;
import com.example.demo.model.Product;
import com.example.demo.repository.AuditRepository;
import com.example.demo.service.SyncScrapperService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Queue;

@RestController
@AllArgsConstructor
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final SyncScrapperService syncService;
    private final AuditRepository auditRepository;

    @GetMapping(value = "/v1/fetch/products", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Product>> getProducts(@RequestParam(required = true) String searchKey) {
        LOGGER.debug("EndPoint:- API for initiating and scraping products");
        return syncService.scrapProductDetails(searchKey).buffer(2);
    }

    @GetMapping(value = "/v1/search/products", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Product>> searchProducts(@RequestParam(required = true) String searchKey) {
        LOGGER.debug("EndPoint:- API for initiating and scraping products");
        return syncService.queryProductDetails(searchKey).buffer(4);
    }

    @GetMapping("/v1/recent")
    public List<AuditLog> fetchRecentProducts() {
        LOGGER.debug("EndPoint:- API for fetching the Recent Audits");
        final Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        final Pageable pageable = PageRequest.of(0, 10, sort);
        return auditRepository.findAll(pageable).getContent();
    }
}
