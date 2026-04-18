package com.platform.support.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.support.domain.model.TicketCategory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends BaseRepository<TicketCategory> {
    Optional<TicketCategory> findByName(String name);
    List<TicketCategory> findByActiveTrue();
}