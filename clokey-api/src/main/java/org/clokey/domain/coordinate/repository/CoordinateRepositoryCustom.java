package org.clokey.domain.coordinate.repository;

import org.clokey.domain.coordinate.dto.response.DailyCoordinateListResponse;
import org.clokey.global.paging.SortDirection;
import org.springframework.data.domain.Slice;

public interface CoordinateRepositoryCustom {
    Slice<DailyCoordinateListResponse> findAllDailyCoordinateByMemberId(
            Long currentMemberId, Long lastCoordinateId, int size, SortDirection direction);
}
