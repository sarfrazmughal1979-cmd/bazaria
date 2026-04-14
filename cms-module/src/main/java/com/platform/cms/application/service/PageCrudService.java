package com.platform.cms.application.service;
import com.platform.core.service.AbstractCrudService;
import com.platform.cms.domain.model.Page;
import com.platform.cms.domain.repository.PageRepository;
import org.springframework.stereotype.Service;
@Service
public class PageCrudService extends AbstractCrudService<Page, PageRepository> {
    public PageCrudService(PageRepository repository) { super(repository, "Page"); }
}
