package com.platform.iam.application.service;
import com.platform.core.service.AbstractCrudService;
import com.platform.iam.domain.model.Role;
import com.platform.iam.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
@Service
public class RoleCrudService extends AbstractCrudService<Role, RoleRepository> {
    public RoleCrudService(RoleRepository repository) { super(repository, "Role"); }
}
