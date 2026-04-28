package com.platform.search.application.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.platform.search.domain.model.ProductDocument;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public Page<ProductDocument> search(String keyword, String categoryId, Double minPrice, Double maxPrice,
                                        String sortBy, String sortDir, int page, int size) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Keyword search
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

        // Price range filter â€“ using NumberRangeQuery
        if (minPrice != null || maxPrice != null) {
            NumberRangeQuery.Builder nrBuilder = new NumberRangeQuery.Builder()
                    .field("effectivePrice");
            if (minPrice != null) nrBuilder.gte(minPrice.doubleValue());
            if (maxPrice != null) nrBuilder.lte(maxPrice.doubleValue());
            boolQuery.filter(Query.of(q -> q.range(r -> r.number(nrBuilder.build()))));
        }

        // Sorting
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "_score");

        // Build and execute query
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery.build()))
                .withPageable(PageRequest.of(page, size))
                .withSort(sort)
                .build();

        SearchHits<ProductDocument> hits = elasticsearchTemplate.search(searchQuery, ProductDocument.class);
        List<ProductDocument> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new PageImpl<>(content, PageRequest.of(page, size), hits.getTotalHits());
    }

    public Page<ProductDocument> findFeatured(int page, int size) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.term(t -> t.field("featured").value(true)))
                .withPageable(PageRequest.of(page, size))
                .build();
        SearchHits<ProductDocument> hits = elasticsearchTemplate.search(query, ProductDocument.class);
        List<ProductDocument> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new PageImpl<>(content, PageRequest.of(page, size), hits.getTotalHits());
    }

        public List<String> autoComplete(String prefix, int size) {
            try {
                CompletionSuggester completionSuggester = CompletionSuggester.of(c -> c
                        .field("suggest")
                        .size(size)
                        .skipDuplicates(true)
                        .fuzzy(f -> f.fuzziness("AUTO"))
                );

                FieldSuggester fieldSuggester = FieldSuggester.of(f -> f.completion(completionSuggester));
                Suggester suggester = Suggester.of(s -> s.suggesters("product-suggest", fieldSuggester));

                SearchRequest request = SearchRequest.of(sr -> sr
                        .index("products")
                        .suggest(suggester)
                        .query(Query.of(q -> q.matchAll(m -> m)))
                );

                SearchResponse<ProductDocument> response = elasticsearchTemplate.execute(
                        client -> client.search(request, ProductDocument.class)
                );

                return response.suggest().get("product-suggest").stream()
                        .filter(Suggestion::isCompletion)
                        .flatMap(s -> s.completion().options().stream())
                        .map(opt -> opt.text())
                        .distinct()
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Autocomplete failed for prefix '{}'", prefix, e);
                return List.of();
            }
    }
}