package org.clokey.domain.auth.service;

import org.clokey.domain.auth.dto.response.UserInfoResponse;
import org.clokey.member.enums.OauthProvider;

public interface AuthService {

    UserInfoResponse getUserInfo(String code, OauthProvider oauthProvider, String referer);
}
