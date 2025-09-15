package org.clokey.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.report.dto.request.ReportCreateRequest;
import org.clokey.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/report")
@Tag(name = "13. 신고 API", description = "댓글 및 기록 신고 API입니다.")
@RequiredArgsConstructor
public class ReportController {

    @PostMapping
    @Operation(summary = "신고 생성", description = "신고를 생성합니다.")
    public BaseResponse<Void> createNewReport(@Valid @RequestBody ReportCreateRequest reportCreatRequest) {
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.CREATED, )
    }
}
