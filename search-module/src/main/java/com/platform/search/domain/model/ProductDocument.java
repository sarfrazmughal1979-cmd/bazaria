package com.platform.search.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.math.BigDecimal;

@Document(indexName = "products")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Keyword)
    private String slug;

    @Field(type = FieldType.Keyword)
    private String sku;

    @MultiField(
        mainField = @Field(type = FieldType.Text, name = "description"),
        otherFields = {
            @InnerField(suffix = "keyword", type = FieldType.Keyword)
        }
    )
    private String description;

    @Field(type = FieldType.Text)
    private String shortDescription;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Text)
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String brandId;

    @Field(type = FieldType.Text)
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String vendorId;

    @Field(type = FieldType.Text)
    private String vendorName;

    @Field(type = FieldType.Double)
    private BigDecimal basePrice;

    @Field(type = FieldType.Double)
    private BigDecimal effectivePrice;

    @Field(type = FieldType.Double)
    private BigDecimal salePrice;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Double)
    private BigDecimal averageRating;

    @Field(type = FieldType.Integer)
    private int reviewCount;

    @Field(type = FieldType.Long)
    private long soldCount;

    @Field(type = FieldType.Boolean)
    private boolean inStock;

    @Field(type = FieldType.Boolean)
    private boolean featured;

    @Field(type = FieldType.Keyword)
    private String primaryImage;

    @CompletionField(maxInputLength = 100)
    private Completion suggest;
}