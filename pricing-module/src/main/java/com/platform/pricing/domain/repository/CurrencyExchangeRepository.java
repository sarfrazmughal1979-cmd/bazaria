package com.platform.pricing.domain.repository;

import com.platform.pricing.domain.model.CurrencyExchange;
import com.platform.core.repository.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CurrencyExchangeRepository extends BaseRepository<CurrencyExchange> {
    Optional<CurrencyExchange> findByFromCurrencyAndToCurrency(String from, String to);
}