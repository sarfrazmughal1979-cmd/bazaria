package com.platform.iam.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.iam.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends SoftDeleteRepository<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByOauthProviderAndOauthId(String provider, String oauthId);

    Page<User> findByDeletedFalse(Pageable pageable);
}