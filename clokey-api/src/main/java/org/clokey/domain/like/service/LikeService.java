package org.clokey.domain.like.service;

import org.clokey.domain.like.dto.response.LikedHistoriesResponse;
import org.clokey.response.SliceResponse;
import org.springframework.data.domain.Pageable;

public interface LikeService {
    SliceResponse<LikedHistoriesResponse.LikedHistoryPreview> getLikedHistories(Pageable pageable);
}
