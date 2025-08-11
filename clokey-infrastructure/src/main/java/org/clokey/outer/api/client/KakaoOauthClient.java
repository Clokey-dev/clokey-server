package org.clokey.outer.api.client;

import org.clokey.outer.api.config.KakaoAuthConfig;
import org.clokey.outer.api.dto.KakaoTokenDto;
import org.clokey.outer.api.dto.OIDCPublicKeysDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "KakaoAuthClient",
        url = "https://kauth.kakao.com",
        configuration = KakaoAuthConfig.class)
public interface KakaoOauthClient {

    @PostMapping(
            "/oauth/token?grant_type=authorization_code&client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}&code={CODE}&client_secret={CLIENT_SECRET}")
    KakaoTokenDto getKakaoTokens(
            @PathVariable("CLIENT_ID") String clientId,
            @PathVariable("REDIRECT_URI") String redirectUri,
            @PathVariable("CODE") String code,
            @PathVariable("CLIENT_SECRET") String client_secret);

    @Cacheable(cacheNames = "KakaoOIDC", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OIDCPublicKeysDto getKakaoOIDCOpenKeys();
}
