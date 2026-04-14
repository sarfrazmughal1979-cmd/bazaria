package com.platform.order.application.service;
import com.platform.core.service.AbstractCrudService;
import com.platform.order.domain.model.ReturnRequest;
import com.platform.order.domain.repository.ReturnRequestRepository;
import org.springframework.stereotype.Service;
@Service
public class ReturnRequestCrudService extends AbstractCrudService<ReturnRequest, ReturnRequestRepository> {
    public ReturnRequestCrudService(ReturnRequestRepository repository) { super(repository, "ReturnRequest"); }
}
