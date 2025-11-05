package org.clokey.domain.history.repository;

import org.clokey.history.entity.HistoryClothTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryClothTagRepository extends JpaRepository<HistoryClothTag, Long> {
    List<HistoryClothTag> findByHistoryImageId(Long historyImageId);
}
