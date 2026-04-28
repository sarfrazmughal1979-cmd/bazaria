package com.platform.promotion.application.mapper;

import com.platform.promotion.application.dto.FlashSaleRequest;
import com.platform.promotion.domain.model.FlashSale;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    // Map DTO -> Entity (basic)
    public FlashSale toEntity(FlashSaleRequest request) {
        return FlashSale.builder()
                .name(request.getName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(false)
                .build();
    }
}
