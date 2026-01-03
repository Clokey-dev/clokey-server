package org.clokey.domain.cloth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.cloth.dto.request.ClothImagesUploadRequest;
import org.clokey.domain.cloth.dto.response.ClothImagesPresignedUrlResponse;
import org.clokey.domain.cloth.service.ClothAiService;
import org.clokey.response.BaseResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloth-ai")
@RequiredArgsConstructor
@Tag(name = "17. 옷 AI API", description = "옷 AI 관련 API입니다.")
@Validated
@Transactional(readOnly = true)
public class ClothAiController {

    private final ClothAiService clothAiService;

    @PostMapping("/images")
    @Operation(
            operationId = "ClothAi_getClothUploadPresignedUrl",
            summary = "옷 이미지 업로드용 presignedUrl 발급",
            description = "옷 이미지 업로드용 presignedUrl을 발급합니다.")
    public BaseResponse<ClothImagesPresignedUrlResponse> getClothUploadPresignedUrl(
            @Valid @RequestBody ClothImagesUploadRequest request) {
        ClothImagesPresignedUrlResponse response =
                clothAiService.getClothUploadPresignedUrls(request);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.CREATED, response);
    }
}
