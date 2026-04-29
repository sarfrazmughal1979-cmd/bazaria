package com.platform.search.application.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.JsonData;
import com.platform.search.domain.model.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchClient elasticsearchClient;   // <-- auto‑configured bean

    // ---------- Search ----------
    public Page<ProductDocument> search(String keyword, String categoryId, Double minPrice, Double maxPrice,
                                        String sortBy, String sortDir, int page, int size) {

        // Build the bool query
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Keyword
        if (keyword != null && !keyword.isBlank()) {
            boolQuery.must(Query.of(q -> q.multiMatch(MultiMatchQuery.of(mm -> mm
                    .query(keyword)
                    .fields("name^3", "description^1.5", "shortDescription^2", "categoryName", "brandName")
            ))));
        }

        // Category filter
        if (categoryId != null) {
            boolQuery.filter(Query.of(q -> q.term(t -> t.field("categoryId").value(categoryId))));
        }

        // Price range
        if (minPrice != null || maxPrice != null) {
            NumberRangeQuery.Builder rangeBuilder = new NumberRangeQuery.Builder()
                    .field("effectivePrice");
            if (minPrice != null) rangeBuilder.gte(minPrice.doubleValue());
            if (maxPrice != null) rangeBuilder.lte(maxPrice.doubleValue());
            boolQuery.filter(Query.of(q -> q.range(r -> r.number(rangeBuilder.build()))));
        }

        // Sorting (default _score)
        String sortField = (sortBy != null) ? sortBy : "_score";
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        try {
            SearchRequest searchRequest = SearchRequest.of(sr -> sr
                    .index("products")
                    .query(q -> q.bool(boolQuery.build()))
                    .from(page * size)
                    .size(size)
                    .sort(s -> s.field(f -> f.field(sortField).order(descending ? SortOrder.Desc : SortOrder.Asc)))
            );

            SearchResponse<ProductDocument> response = elasticsearchClient.search(searchRequest, ProductDocument.class);

            List<ProductDocument> content = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

            long totalHits = response.hits().total() != null ? response.hits().total().value() : 0;
            return new PageImpl<>(content, PageRequest.of(page, size), totalHits);
        } catch (Exception e) {
            log.error("Search failed", e);
            return Page.empty();
        }
    }

    // ---------- Featured ----------
    public Page<ProductDocument> findFeatured(int page, int size) {
        try {
            SearchRequest request = SearchRequest.of(sr -> sr
                    .index("products")
                    .query(q -> q.term(t -> t.field("featured").value(true)))
                    .from(page * size)
                    .size(size)
            );

            SearchResponse<ProductDocument> response = elasticsearchClient.search(request, ProductDocument.class);
            List<ProductDocument> content = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
            long total = response.hits().total() != null ? response.hits().total().value() : 0;
            return new PageImpl<>(content, PageRequest.of(page, size), total);
        } catch (Exception e) {
            log.error("Featured search failed", e);
            return Page.empty();
        }
    }

    // ---------- Autocomplete ----------
    public List<String> autoComplete(String prefix, int size) {
        try {
            SearchRequest request = SearchRequest.of(sr -> sr
                    .index("products")
                    .suggest(s -> s
                            .suggesters("product-suggest", FieldSuggester.of(f -> f
                                    .completion(CompletionSuggester.of(c -> c
                                            .field("suggest")
                                            .size(size)
                                            .skipDuplicates(true)
                                            .fuzzy(fuzzy -> fuzzy.fuzziness("AUTO"))
                                    ))
                            ))
                    )
                    .query(Query.of(q -> q.matchAll(m -> m)))
            );

            SearchResponse<ProductDocument> response = elasticsearchClient.search(request, ProductDocument.class);

            return response.suggest().get("product-suggest").stream()
                    .filter(Suggestion::isCompletion)
                    .flatMap(s -> s.completion().options().stream())
                    .map(opt -> opt.text())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Autocomplete failed", e);
            return List.of();
        }
    }

    // ---------- Index a product (called from CatalogSyncListener) ----------
    public void indexProduct(ProductDocument product) {
        try {
            elasticsearchClient.index(i -> i
                    .index("products")
                    .id(product.getId())
                    .document(product)
            );
            log.debug("Indexed product {}", product.getId());
        } catch (Exception e) {
            log.error("Failed to index product {}", product.getId(), e);
        }
    }

    // ---------- Delete a product from index ----------
    public void deleteProduct(String productId) {
        try {
            elasticsearchClient.delete(d -> d.index("products").id(productId));
            log.debug("Deleted product {} from index", productId);
        } catch (Exception e) {
            log.error("Failed to delete product {}", productId, e);
        }
    }
}