package org.clokey.domain.cloth.service;

import org.clokey.cloth.enums.Season;
import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothCreateResponse;
import org.clokey.domain.cloth.dto.response.ClothRecommendListResponse;
import org.clokey.response.SliceResponse;

public interface ClothService {

    ClothCreateResponse createClothes(ClothCreateRequests list);

    SliceResponse<ClothRecommendListResponse> recommendCategoryClothes(
            Long lastClothId, int size, Long categoryId, Season season);
}
