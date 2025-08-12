package org.clokey.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.clokey.domain.auth.dto.response.UserInfoResponse;
import org.clokey.domain.auth.util.AppleUtil;
import org.clokey.member.enums.OauthProvider;
import org.clokey.outer.api.client.AppleOauthClient;
import org.clokey.outer.api.client.KakaoOauthClient;
import org.clokey.outer.api.dto.AppleTokenDto;
import org.clokey.outer.api.dto.KakaoTokenDto;
import org.clokey.properties.OauthProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final KakaoOauthClient kakaoOauthClient;
    private final AppleOauthClient appleOauthClient;
    private final AppleUtil appleUtil;

    private final OauthProperties oauthProperties;

    @Override
    public UserInfoResponse getUserInfo(String code, OauthProvider oauthProvider, String referer) {

        if (oauthProvider.equals(OauthProvider.KAKAO)) {
            return UserInfoResponse.from(getKakaoTokens(code, referer));
        }
        return UserInfoResponse.from(getAppleTokens(code, referer));
    }

    private KakaoTokenDto getKakaoTokens(String code, String referer) {
        return kakaoOauthClient.getKakaoTokens(
                oauthProperties.kakao().clientId(),
                referer + "/kakao/callback",
                code,
                oauthProperties.kakao().clientSecret());
    }

    private AppleTokenDto getAppleTokens(String code, String referer) {

        return appleOauthClient.getAppleTokens(
                oauthProperties.apple().clientId(),
                appleUtil.getClientSecret(),
                code,
                "authorization_code",
                referer + "/apple/callback");
    }
}
