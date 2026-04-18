package com.platform.analytics.application.service;

import com.platform.analytics.domain.model.CustomerMetric;
import com.platform.analytics.domain.repository.CustomerMetricRepository;
import com.platform.core.service.AbstractCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class CustomerMetricService extends AbstractCrudService<CustomerMetric, CustomerMetricRepository> {
    protected CustomerMetricService(CustomerMetricRepository repository) {
        super(repository, CustomerMetric.class.getSimpleName());
    }
}
