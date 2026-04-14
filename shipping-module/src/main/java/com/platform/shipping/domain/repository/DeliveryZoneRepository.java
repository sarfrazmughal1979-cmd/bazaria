package com.platform.shipping.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.shipping.domain.model.DeliveryZone;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryZoneRepository extends BaseRepository<DeliveryZone> {
    List<DeliveryZone> findByActiveTrue();
    Optional<DeliveryZone> findByCountryAndCityAndActiveTrue(String country, String city);
}