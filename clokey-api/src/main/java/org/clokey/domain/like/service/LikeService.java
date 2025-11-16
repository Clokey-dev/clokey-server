package org.clokey.domain.like.service;

import java.awt.print.Pageable;
import org.clokey.domain.like.dto.response.LikedHistoriesResponse;

public interface LikeService {
    LikedHistoriesResponse getLikedHistories(Pageable pageable);
}
