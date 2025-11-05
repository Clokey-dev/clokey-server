package org.clokey.domain.history.repository;

import org.clokey.history.entity.HistoryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {
    List<HistoryImage> findByHistoryId(Long historyId);
}
