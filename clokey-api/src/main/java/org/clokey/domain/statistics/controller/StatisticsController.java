package org.clokey.domain.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.statistics.dto.response.FavoriteCategoryItemsResponse;
import org.clokey.domain.statistics.dto.response.StatisticsCheckConditionResponse;
import org.clokey.domain.statistics.service.StatisticsService;
import org.clokey.response.BaseResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "16. 통계 API", description = "통계 관련 API입니다.")
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/check-conditions")
    @Operation(summary = "통계 최소 조건 확인", description = "통계 집계가 가능한 최소 조건을 확인하는 API입니다.")
    public BaseResponse<StatisticsCheckConditionResponse> checkStatisticsCondition() {
        StatisticsCheckConditionResponse response = statisticsService.checkStatisticsCondition();
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, response);
    }

    @GetMapping("/favorite-category-items")
    @Operation(summary = "카테고리별 최애 아이템 조회", description = "카테고리별 아이템의 개수와 점유율을 조회하는 API입니다..")
    public BaseResponse<FavoriteCategoryItemsResponse> getFavoriteCategoryItems(
            @Parameter(description = "카테고리 ID") @RequestParam Long categoryId) {
        FavoriteCategoryItemsResponse response =
                statisticsService.getFavoriteCategoryItems(categoryId);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, response);
    }
}
