package com.platform.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Money {

    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currencyCode;

    public static Money of(BigDecimal amount, String currency) {
        return new Money(
                amount.setScale(4, RoundingMode.HALF_UP),
                currency
        );
    }

    public static Money of(double amount, String currency) {
        return of(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), currency);
    }

    public static Money bdt(BigDecimal amount) {
        return of(amount, "BDT");
    }

    public static Money bdt(double amount) {
        return of(BigDecimal.valueOf(amount), "BDT");
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currencyCode);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currencyCode);
    }

    public Money multiply(int multiplier) {
        return new Money(
                this.amount.multiply(BigDecimal.valueOf(multiplier))
                        .setScale(4, RoundingMode.HALF_UP),
                this.currencyCode
        );
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(
                this.amount.multiply(multiplier).setScale(4, RoundingMode.HALF_UP),
                this.currencyCode
        );
    }

    public Money percentage(BigDecimal percent) {
        return multiply(percent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public Money negate() {
        return new Money(this.amount.negate(), this.currencyCode);
    }

    private void assertSameCurrency(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException(
                    "Cannot operate on different currencies: "
                            + this.currencyCode + " vs " + other.currencyCode
            );
        }
    }

    @Override
    public String toString() {
        return currencyCode + " " + amount.setScale(2, RoundingMode.HALF_UP);
    }
}