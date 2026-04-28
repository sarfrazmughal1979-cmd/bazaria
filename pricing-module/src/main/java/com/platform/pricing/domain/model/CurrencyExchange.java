package com.platform.pricing.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currency_exchange_rates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CurrencyExchange extends BaseEntity {

    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;

    @Column(name = "rate", precision = 19, scale = 6, nullable = false)
    private BigDecimal rate;

    @Column(name = "source", length = 50)
    private String source;   // e.g., "ECB", "manual"
}