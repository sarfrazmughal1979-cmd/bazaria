package com.platform.iam.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.core.repository.SoftDeleteRepository;
import com.platform.iam.domain.model.Permission;
import com.platform.iam.domain.model.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends SoftDeleteRepository<Permission> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);
}