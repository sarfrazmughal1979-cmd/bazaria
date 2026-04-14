package com.platform.analytics.api;

import com.platform.analytics.domain.model.CustomerMetric;
import com.platform.analytics.domain.repository.CustomerMetricRepository;
import com.platform.core.api.AbstractCrudController;
import com.platform.core.service.AbstractCrudService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerMatricController extends AbstractCrudController<CustomerMetric, CustomerMetricRepository> {
    protected CustomerMatricController(AbstractCrudService<CustomerMetric, CustomerMetricRepository> service) {
        super(service);
    }

    @Override
    protected String getServiceName() {
        return "CustomerMatric";
    }
}
