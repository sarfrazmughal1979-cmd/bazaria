package com.platform.iam.application.service;
import com.platform.core.service.AbstractCrudService;
import com.platform.iam.domain.model.Permission;
import com.platform.iam.domain.repository.PermissionRepository;
import org.springframework.stereotype.Service;
@Service
public class PermissionCrudService extends AbstractCrudService<Permission, PermissionRepository> {
    public PermissionCrudService(PermissionRepository repository) { super(repository, "Permission"); }
}
