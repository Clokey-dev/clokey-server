package org.clokey.domain.cloth.repository;

import org.clokey.cloth.enums.Season;
import org.clokey.domain.cloth.dto.response.ClothRecommendListResponse;
import org.springframework.data.domain.Slice;

public interface ClothRepositoryCustom {

    Slice<ClothRecommendListResponse> findAllClothesByCategoryAndSeason(
            Long lastClothId, int size, Long categoryId, Season season);
}
