package com.platform.iam.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorRegistrationRequest {

    @NotBlank(message = "Shop name is required")
    @Size(min = 3, max = 255)
    private String shopName;

    private String shopDescription;

    @NotBlank(message = "Business address is required")
    private String businessAddressLine1;

    private String businessAddressLine2;

    @NotBlank
    private String city;

    private String state;

    private String postalCode;

    @NotBlank
    private String country;

    private String businessRegistrationNumber;

    private String taxId;

    @NotBlank(message = "Bank account name is required")
    private String bankAccountName;

    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    private String bankRoutingNumber;
}