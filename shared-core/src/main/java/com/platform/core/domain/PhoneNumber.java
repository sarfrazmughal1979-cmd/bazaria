package com.platform.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneNumber {

    @Column(name = "country_code", length = 5)
    private String countryCode;

    @Column(name = "number", length = 20)
    private String number;

    public String getFullNumber() {
        return countryCode + number;
    }

    @Override
    public String toString() {
        return getFullNumber();
    }
}