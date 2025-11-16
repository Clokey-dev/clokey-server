package org.clokey.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.like.dto.response.LikedHistoriesResponse;
import org.clokey.domain.like.service.LikeService;
import org.clokey.response.BaseResponse;
import org.clokey.response.SliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Tag(name = "9. 좋아요 API", description = "좋아요 관련 API입니다.")
@Validated
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/histories")
    @Operation(summary = "좋아요한 기록 조회", description = "사용자가 좋아요한 기록을 조회합니다.")
    public BaseResponse<SliceResponse<LikedHistoriesResponse.LikedHistoryPreview>>
            getLikedHistories(@PageableDefault(size = 10) Pageable pageable) {
        SliceResponse<LikedHistoriesResponse.LikedHistoryPreview> response =
                likeService.getLikedHistories(pageable);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, response);
    }
}
