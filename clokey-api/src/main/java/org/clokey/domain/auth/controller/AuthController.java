package org.clokey.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clokey.code.GlobalBaseSuccessCode;
import org.clokey.domain.auth.dto.response.UserInfoResponse;
import org.clokey.domain.auth.service.AuthService;
import org.clokey.member.enums.OauthProvider;
import org.clokey.response.BaseResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1-1. 인증 API", description = "인증 관련 API입니다.")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/user-info")
    @Operation(
            summary = "회원 정보 요청 API",
            description = "회원 정보가 담긴 ID Token을 요청합니다. Refer은 redirection을 위한 목적이고 입력하지 않으셔도 됩니다.")
    public BaseResponse<UserInfoResponse> getUserInfo(
            @RequestParam("code") String code,
            @RequestParam("oauth-provider") OauthProvider oauthProvider,
            @RequestHeader(value = "referer", required = false) String referer) {

        UserInfoResponse response = authService.getUserInfo(code, oauthProvider, referer);

        return BaseResponse.onSuccess(GlobalBaseSuccessCode.OK, response);
    }

    // 유저 상황을 확인하는 API

    // 회원 가입 API

    // 로그인 API

    //    @GetMapping("/login")
    //    @Operation(summary = "로그인 API", description = "로그인을 할 수 있는 API 입니다.")
    //    public BaseResponse<Void> loginUser(@Valid @RequestBody LoginRequest request) {
    //        authService.loginUser(request);
    //        return BaseResponse.onSuccess(GlobalBaseSuccessCode.NO_CONTENT, null);
    //    }
}
