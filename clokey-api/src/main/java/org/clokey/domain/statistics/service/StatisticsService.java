package org.clokey.domain.statistics.service;

import org.clokey.domain.statistics.dto.response.FavoriteCategoryItemsResponse;
import org.clokey.domain.statistics.dto.response.FavoriteItemsResponse;
import org.clokey.domain.statistics.dto.response.StatisticsCheckConditionResponse;

public interface StatisticsService {
    StatisticsCheckConditionResponse checkStatisticsCondition();

    FavoriteCategoryItemsResponse getFavoriteCategoryItems(Long categoryId);

    FavoriteItemsResponse getFavoriteItems();
}
