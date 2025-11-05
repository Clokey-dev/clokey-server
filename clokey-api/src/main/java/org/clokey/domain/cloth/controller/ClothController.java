package org.clokey.domain.cloth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clokey.cloth.enums.Season;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothCreateResponse;
import org.clokey.domain.cloth.dto.response.ClothRecommendListResponse;
import org.clokey.domain.cloth.service.ClothService;
import org.clokey.global.annotation.PageSize;
import org.clokey.response.BaseResponse;
import org.clokey.response.SliceResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clothes")
@RequiredArgsConstructor
@Tag(name = "3. 옷 API", description = "옷 관련 API입니다.")
@Validated
public class ClothController {

    private final ClothService clothService;

    @PostMapping
    @Operation(summary = "옷 생성", description = "새로운 옷을 생성합니다.")
    public BaseResponse<ClothCreateResponse> createClothes(
            @Valid @RequestBody ClothCreateRequests request) {
        ClothCreateResponse response = clothService.createClothes(request);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.CREATED, response);
    }

    @GetMapping("/recommend")
    @Operation(summary = "카테고리별 계절에 맞는 옷 조회", description = "카테고리별로 계절에 맞는 옷을 조회하는 API입니다.")
    public BaseResponse<SliceResponse<ClothRecommendListResponse>> recommendCategoryClothes(
            @Parameter(description = "이전 페이지의 옷ID (첫 요청 시 생략)") @RequestParam(required = false)
                    Long lastClothId,
            @Parameter(description = "페이지당 조회할 옷 수") @RequestParam @PageSize Integer size,
            @Parameter(description = "옷 카테고리 ID") @RequestParam Long categoryId,
            @Parameter(description = "요청 계절") @RequestParam Season season) {
        SliceResponse<ClothRecommendListResponse> response =
                clothService.recommendCategoryClothes(lastClothId, size, categoryId, season);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, response);
    }
}
