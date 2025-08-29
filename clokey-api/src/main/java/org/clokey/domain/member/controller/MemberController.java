package org.clokey.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.member.dto.request.DuplicatedIdCheckRequest;
import org.clokey.domain.member.dto.request.ProfileUpdateRequest;
import org.clokey.domain.member.dto.response.DuplicatedIdCheckResponse;
import org.clokey.domain.member.service.MemberService;
import org.clokey.response.BaseResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "3. 멤버 API", description = "멤버 관련 API입니다.")
@Validated
public class MemberController {

    private final MemberService memberService;

    @PatchMapping
    @Operation(summary = "프로필 수정", description = "프로필을 수정/추가 합니다.")
    public BaseResponse<Void> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        memberService.updateProfile(request);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.NO_CONTENT, null);
    }

    @PostMapping("/check-duplicate-id")
    @Operation(summary = "아이디 중복확인", description = "클로키아이디 중복을 확인합니다.")
    public BaseResponse<DuplicatedIdCheckResponse> checkDuplicateClokeyId(
            @Valid @RequestBody DuplicatedIdCheckRequest request) {

        DuplicatedIdCheckResponse response = memberService.checkDuplicateClokeyId(request);
        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, response);
    }

    @PostMapping("/follow")
    @Operation(
            summary = "팔로우 API",
            description = "다른 사용자를 팔로우/언팔로우하는 API입니다. 비공개 계정에 팔로우시 요청이 들어갑니다.")
    public BaseResponse<Void> follow(@RequestParam("clokeyId") String clokeyId) {

        memberService.follow(clokeyId);

        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, null);
    }
    // 비공개 사용자에게 팔로우시 추가적인 요소를 반환해야?
    // 클로키아이디가 아닌 멤버ID를 받아야?
    // 차단한 회원의 팔로우시? 추후구현?
}
