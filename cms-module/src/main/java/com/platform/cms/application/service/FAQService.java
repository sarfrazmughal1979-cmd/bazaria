package com.platform.cms.application.service;

import com.platform.cms.application.dto.FAQRequest;
import com.platform.cms.application.dto.FAQResponse;
import com.platform.cms.application.mapper.CmsMapper;
import com.platform.cms.domain.model.FAQ;
import com.platform.cms.domain.model.FAQCategory;
import com.platform.cms.domain.repository.FAQCategoryRepository;
import com.platform.cms.domain.repository.FAQRepository;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;
    private final FAQCategoryRepository categoryRepository;
    private final CmsMapper mapper;

    // FAQ Categories
    @Transactional
    @CacheEvict(value = "faq", allEntries = true)
    public FAQCategory createCategory(String name, String slug, String description) {
        if (categoryRepository.existsBySlugAndDeletedFalse(slug)) {
            throw new BusinessException("DUPLICATE_SLUG", "Category slug already exists");
        }
        FAQCategory category = FAQCategory.builder()
            .name(name)
            .slug(slug)
            .description(description)
            .visible(true)
            .build();
        return categoryRepository.save(category);
    }

    @Cacheable(value = "faq", key = "'categories'")
    public List<FAQCategory> getVisibleCategories() {
        return categoryRepository.findByVisibleTrueAndDeletedFalseOrderBySortOrderAsc();
    }

    // FAQs
    @Transactional
    @CacheEvict(value = "faq", allEntries = true)
    public FAQResponse createFAQ(FAQRequest request) {
        FAQCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("FAQCategory", "id", request.getCategoryId()));
        }

        FAQ faq = mapper.toEntity(request);
        faq.setCategory(category);
        faq = faqRepository.save(faq);

        return mapper.toResponse(faq);
    }

    @Cacheable(value = "faq", key = "'category:' + #categoryId")
    public List<FAQResponse> getFAQsByCategory(UUID categoryId) {
        return faqRepository.findByCategoryIdAndVisibleTrueOrderBySortAsc(categoryId).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "faq", key = "'search:' + #keyword")
    public PagedResponse<FAQResponse> searchFAQs(String keyword, Pageable pageable) {
        var page = faqRepository.searchByKeyword(keyword, pageable);
        return PagedResponse.from(page.map(mapper::toResponse));
    }

    @Transactional
    public void markHelpful(UUID faqId) {
        faqRepository.incrementHelpful(faqId);
    }

    @Transactional
    public void markNotHelpful(UUID faqId) {
        faqRepository.incrementNotHelpful(faqId);
    }
}