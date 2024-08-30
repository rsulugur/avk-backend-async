package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.Scrapper;
import com.example.demo.utils.AppUtils;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class EbayScrapper implements Scrapper {
    static final String PRODUCT_URL = "https://www.ebay.com/sch/i.html?&LH_BIN=1&_nkw={productName}";
    private static final Logger logger = LoggerFactory.getLogger(EbayScrapper.class);

    @Override
    public Flux<Product> crawl(String productName) {
        final String formattedUrl = PRODUCT_URL.replace("{productName}", productName);
        logger.info("Scrapper: Started Amazon Scrapping on URL {}", formattedUrl);

        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");  // Run in headless mode
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            WebDriver webDriver = new ChromeDriver(options);
            webDriver.get(formattedUrl);
            final List<WebElement> elements = webDriver.findElements(By.cssSelector("li.s-item.s-item__pl-on-bottom"));

            return Flux.fromIterable(elements)
                    .map(webElement -> {
                        final Product prod = new Product();
                        try {
                            WebElement textElement = webElement.findElement(By.cssSelector(".s-item__title"));
                            prod.setProductName(textElement.getText());

                            WebElement priceElement = webElement.findElement(By.cssSelector(".s-item__price"));
                            prod.setPrice(AppUtils.convertPrice(priceElement.getText()));

                            WebElement productURL = webElement.findElement(By.cssSelector(".s-item__link"));
                            prod.setUrl(AppUtils.shortenURL(productURL.getAttribute("href")));

                            WebElement productDivImage = webElement.findElement(By.cssSelector("div.s-item__image-wrapper"));
                            final WebElement img = productDivImage.findElement(By.tagName("img"));
                            prod.setImage(img.getAttribute("src"));

                            prod.setSource("ebay");
                            prod.setSuggestedProductName(productName);

                            return prod;
                        } catch (NoSuchElementException ex) {
                            logger.warn("Unable to Scrap Product {}", ex.getMessage());
                            return prod;
                        }
                    })
                    .filter(Product::isValid)
                    .doAfterTerminate(webDriver::quit);
        } catch (WebDriverException ex) {
            logger.error("Unable to initiate Product Scrapper due to {}", ex.getRawMessage());
            return Flux.empty();
        }
    }
}