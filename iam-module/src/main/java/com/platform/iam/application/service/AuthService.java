package com.platform.iam.application.service;

import com.platform.common.domain.event.UserRegisteredEvent;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.security.UserContext;
import com.platform.iam.application.dto.*;
import com.platform.iam.domain.model.*;
import com.platform.iam.domain.repository.RoleRepository;
import com.platform.iam.domain.repository.UserRepository;
import com.platform.iam.domain.repository.VendorRepository;
import com.platform.iam.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("DUPLICATE_EMAIL", "Email already registered");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default CUSTOMER role not found"));

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .phoneVerified(false)
                .build();
        user.addRole(customerRole);

        user = userRepository.save(user);
        log.info("New user registered: {} [{}]", user.getEmail(), user.getId());

        eventPublisher.publishAsync(new UserRegisteredEvent(
                user.getId().toString(), user.getEmail(), "CUSTOMER"));

        UserContext userContext = buildUserContext(user);
        return buildTokenResponse(userContext, user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS",
                        "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("ACCOUNT_INACTIVE",
                    "Your account is " + user.getStatus().name().toLowerCase());
        }

        user.recordLogin();
        userRepository.save(user);

        UserContext userContext = buildUserContext(user);
        return buildTokenResponse(userContext, user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("INVALID_TOKEN", "Invalid or expired refresh token");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("ACCOUNT_INACTIVE", "Account is not active");
        }

        UserContext userContext = buildUserContext(user);
        return buildTokenResponse(userContext, user);
    }

    private UserContext buildUserContext(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        UUID vendorId = vendorRepository.findByUserId(user.getId())
                .filter(Vendor::isActive)
                .map(Vendor::getId)
                .orElse(null);

        return UserContext.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .permissions(permissions)
                .vendorId(vendorId)
                .build();
    }

    private TokenResponse buildTokenResponse(UserContext userContext, User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(userContext);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userContext.getUserId());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus().name())
                .emailVerified(user.isEmailVerified())
                .roles(userContext.getRoles())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .user(userResponse)
                .build();
    }
}