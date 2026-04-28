package com.platform.settlement.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.domain.Money;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.settlement.application.dto.SubOrderInfo;
import com.platform.settlement.domain.model.Settlement;
import com.platform.settlement.domain.model.SettlementItem;
import com.platform.settlement.domain.model.SettlementStatus;
import com.platform.settlement.domain.model.VendorAccount;
import com.platform.settlement.domain.repository.CommissionRuleRepository;
import com.platform.settlement.domain.repository.SettlementRepository;
import com.platform.settlement.domain.repository.VendorAccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final VendorAccountRepository vendorAccountRepository;
    private final CommissionRuleRepository commissionRuleRepository;
    private final CommissionCalculationService commissionService;
    private final RestClientFactory restClientFactory;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    private ResilientRestClient orderRestClient;

    @PostConstruct
    public void init() {
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
    }

    @Scheduled(cron = "0 0 2 * * *") // Run daily at 2 AM
    @Transactional
    public void generateDailySettlements() {
        log.info("Starting daily settlement generation");

        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant today = Instant.now();

        // Get all delivered sub-orders from yesterday via REST
        List<SubOrderInfo> deliveredSubOrders = orderRestClient.get(
                "/api/v1/orders/sub-orders/delivered?start={start}&end={end}",
                List.class,
                yesterday.toString(), today.toString());

        // Group by vendor and create settlements
        deliveredSubOrders.stream()
                .collect(Collectors.groupingBy(SubOrderInfo::vendorId))
                .forEach((vendorId, subOrders) -> {
                    createSettlement(vendorId, subOrders);
                });
    }

    @Transactional
    public void createSettlement(UUID vendorId, List<SubOrderInfo> subOrders) {
        VendorAccount vendorAccount = vendorAccountRepository
                .findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VendorAccount", "vendorId", vendorId));

        String currency = "PKR";
        Money totalSales = Money.zero(currency);
        Money totalCommission = Money.zero(currency);

        Settlement settlement = Settlement.builder()
                .vendorId(vendorId)
                .status(SettlementStatus.PENDING)
                .periodStart(Instant.now().minus(1, ChronoUnit.DAYS))
                .periodEnd(Instant.now())
                .build();

        for (SubOrderInfo subOrder : subOrders) {
            BigDecimal commissionRate = commissionService
                    .getCommissionRate(vendorId, subOrder.categoryId());

            Money orderAmount = Money.of(subOrder.subtotal(), currency);
            Money commission = orderAmount.percentage(commissionRate);
            Money vendorEarning = orderAmount.subtract(commission);

            SettlementItem item = SettlementItem.builder()
                    .settlement(settlement)
                    .subOrderId(subOrder.subOrderId())
                    .orderAmount(orderAmount)
                    .commissionRate(commissionRate)
                    .commissionAmount(commission)
                    .vendorEarning(vendorEarning)
                    .build();

            settlement.getItems().add(item);
            totalSales = totalSales.add(orderAmount);
            totalCommission = totalCommission.add(commission);
        }

        settlement.setTotalSales(totalSales);
        settlement.setTotalCommission(totalCommission);
        settlement.setTotalPayout(totalSales.subtract(totalCommission));

        settlementRepository.save(settlement);

        // Update vendor account balance
        vendorAccount.setPendingBalance(
                vendorAccount.getPendingBalance().add(settlement.getTotalPayout().getAmount()));
        vendorAccountRepository.save(vendorAccount);

        log.info("Settlement created for vendor {}: sales={}, commission={}, payout={}",
                vendorId, totalSales, totalCommission, settlement.getTotalPayout());
    }
}