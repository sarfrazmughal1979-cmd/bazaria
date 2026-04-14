package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.MenuItem;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends SoftDeleteRepository<MenuItem> {

    List<MenuItem> findByLocationAndParentIsNullAndVisibleTrueAndDeletedFalseOrderBySortOrderAsc(String location);

    List<MenuItem> findByParentIdAndVisibleTrueOrderBySortOrderAsc(String parentId);
}