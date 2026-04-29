package com.platform.search.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("description")
    private String description;

    @JsonProperty("shortDescription")
    private String shortDescription;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("brandId")
    private String brandId;

    @JsonProperty("brandName")
    private String brandName;

    @JsonProperty("vendorId")
    private String vendorId;

    @JsonProperty("vendorName")
    private String vendorName;

    @JsonProperty("basePrice")
    private BigDecimal basePrice;

    @JsonProperty("effectivePrice")
    private BigDecimal effectivePrice;

    @JsonProperty("salePrice")
    private BigDecimal salePrice;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("averageRating")
    private BigDecimal averageRating;

    @JsonProperty("reviewCount")
    private int reviewCount;

    @JsonProperty("soldCount")
    private long soldCount;

    @JsonProperty("inStock")
    private boolean inStock;

    @JsonProperty("featured")
    private boolean featured;

    @JsonProperty("primaryImage")
    private String primaryImage;

    // For completion suggester – must be a JSON object with an "input" array
    @JsonProperty("suggest")
    private Suggest suggest;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Suggest {
        @JsonProperty("input")
        private List<String> input;
        @JsonProperty("weight")
        private int weight;
    }

    // Helper to create suggest entry
    public static Suggest createSuggest(String name) {
        return Suggest.builder()
                .input(List.of(name))
                .weight(1)
                .build();
    }
}