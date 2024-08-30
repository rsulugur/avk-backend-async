package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    private String suggestedProductName;
    private String productName;
    private String image;
    private String source;
    private String ratings;
    private Float price;
    private String url;

    @JsonIgnore
    public boolean isValid() {
        return productName != null && !productName.isEmpty() && price != null && url != null && source != null;
    }
}

