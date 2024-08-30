package com.example.demo.model;

import reactor.core.publisher.Flux;

public interface Scrapper {
    Flux<Product> crawl(final String itemName);
}
