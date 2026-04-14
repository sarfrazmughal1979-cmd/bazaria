package com.platform.iam.application.service;

import com.platform.core.domain.Address;
import com.platform.core.domain.SlugGenerator;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.security.SecurityUtils;
import com.platform.iam.application.dto.*;
import com.platform.common.domain.event.VendorApprovedEvent;
import com.platform.iam.domain.model.*;
import com.platform.iam.domain.repository.RoleRepository;
import com.platform.iam.domain.repository.UserRepository;
import com.platform.iam.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public VendorResponse registerVendor(VendorRegistrationRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();

        if (vendorRepository.existsByUserId(userId)) {
            throw new BusinessException("VENDOR_EXISTS",
                    "You already have a vendor account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String slug = SlugGenerator.generate(request.getShopName());
        if (vendorRepository.existsBySlug(slug)) {
            slug = SlugGenerator.generateUnique(request.getShopName());
        }

        Address businessAddress = Address.builder()
                .addressLine1(request.getBusinessAddressLine1())
                .addressLine2(request.getBusinessAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .build();

        Vendor vendor = Vendor.builder()
                .user(user)
                .shopName(request.getShopName())
                .slug(slug)
                .shopDescription(request.getShopDescription())
                .businessAddress(businessAddress)
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .taxId(request.getTaxId())
                .bankAccountName(request.getBankAccountName())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .bankRoutingNumber(request.getBankRoutingNumber())
                .status(VendorStatus.PENDING)
                .build();

        vendor = vendorRepository.save(vendor);
        log.info("Vendor registration submitted: {} [{}]", vendor.getShopName(), vendor.getId());

        return mapToResponse(vendor);
    }

    @Transactional
    public VendorResponse approveVendor(UUID vendorId, VendorApprovalRequest request) {
        UUID adminId = SecurityUtils.getCurrentUserId();

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));

        if (request.getApproved()) {
            vendor.approve(adminId);

            if (request.getCommissionRate() != null) {
                vendor.setCommissionRate(request.getCommissionRate());
            }

            // Add VENDOR role to user
            Role vendorRole = roleRepository.findByName("VENDOR")
                    .orElseThrow(() -> new RuntimeException("VENDOR role not found"));
            vendor.getUser().addRole(vendorRole);
            userRepository.save(vendor.getUser());

            eventPublisher.publish(new VendorApprovedEvent(
                    vendor.getId().toString(),
                    vendor.getShopName(),
                    vendor.getUser().getId().toString()
            ));

            log.info("Vendor approved: {} [{}] by admin [{}]",
                    vendor.getShopName(), vendor.getId(), adminId);
        } else {
            vendor.reject(request.getReason());
            log.info("Vendor rejected: {} [{}] reason: {}",
                    vendor.getShopName(), vendor.getId(), request.getReason());
        }

        vendor = vendorRepository.save(vendor);
        return mapToResponse(vendor);
    }

    @Transactional(readOnly = true)
    public PagedResponse<VendorResponse> getVendorsByStatus(VendorStatus status, Pageable pageable) {
        Page<Vendor> page = vendorRepository.findByStatus(status, pageable);
        return PagedResponse.from(page.map(this::mapToResponse));
    }

    @Transactional(readOnly = true)
    public VendorResponse getVendorBySlug(String slug) {
        Vendor vendor = vendorRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "slug", slug));
        return mapToResponse(vendor);
    }

    @Transactional(readOnly = true)
    public VendorResponse getCurrentVendor() {
        UUID userId = SecurityUtils.getCurrentUserId();
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "userId", userId));
        return mapToResponse(vendor);
    }

    @Transactional(readOnly = true)
    public VendorResponse findById(UUID vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "vendorId", vendorId.toString()));;
                return mapToResponse(vendor);
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .userId(vendor.getUser().getId())
                .shopName(vendor.getShopName())
                .slug(vendor.getSlug())
                .shopDescription(vendor.getShopDescription())
                .shopLogoUrl(vendor.getShopLogoUrl())
                .shopBannerUrl(vendor.getShopBannerUrl())
                .status(vendor.getStatus().name())
                .commissionRate(vendor.getCommissionRate())
                .rating(vendor.getRating())
                .totalProducts(vendor.getTotalProducts())
                .totalOrders(vendor.getTotalOrders())
                .approvedAt(vendor.getApprovedAt())
                .createdAt(vendor.getCreatedAt())
                .build();
    }
}