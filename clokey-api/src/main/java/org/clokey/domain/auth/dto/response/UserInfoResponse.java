package org.clokey.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.clokey.outer.api.dto.KakaoTokenDto;

public record UserInfoResponse(@Schema(description = "유저를 식별할 수 있는 id_token") String idToken) {
    public static UserInfoResponse createUserInfoResponse(KakaoTokenDto dto) {
        return new UserInfoResponse(dto.idToken());
    }
}
