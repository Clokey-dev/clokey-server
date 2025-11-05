package org.clokey.domain.history.repository;

import org.clokey.history.entity.HistoryStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryStyleRepository extends JpaRepository<HistoryStyle, Long> {
    List<HistoryStyle> findByHistoryId(Long historyId);
}
