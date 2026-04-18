package com.platform.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @NotBlank
    //@Column(name = "address_line1")
    private String addressLine1;

    //@Column(name = "address_line2")
    private String addressLine2;

    @NotBlank
    //@Column(name = "city")
    private String city;

    //@Column(name = "state")
    private String state;

    //@Column(name = "postal_code")
    private String postalCode;

    @NotBlank
    //@Column(name = "country")
    private String country;

    //@Column(name = "latitude")
    private Double latitude;

    //@Column(name = "longitude")
    private Double longitude;

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(addressLine1);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city);
        if (state != null && !state.isBlank()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isBlank()) {
            sb.append(" ").append(postalCode);
        }
        sb.append(", ").append(country);
        return sb.toString();
    }
}