package org.clokey.domain.lookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.response.LookBookCreateResponse;
import org.clokey.domain.lookbook.service.LookBookService;
import org.clokey.response.BaseResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lookbooks")
@RequiredArgsConstructor
@Tag(name = "10. 룩북 API", description = "룩북 관련 API입니다.")
@Validated
public class LookBookController {

    private final LookBookService lookBookService;

    @PostMapping()
    @Operation(summary = "룩북 생성", description = "룩북을 생성하는 API입니다.")
    public BaseResponse<LookBookCreateResponse> createLookBook(
            @Valid @RequestBody LookBookCreateRequest request) {
        LookBookCreateResponse response = lookBookService.createLookBook(request);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.CREATED, response);
    }
}
