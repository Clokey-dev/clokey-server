package org.clokey.outer.api.client;

import org.clokey.outer.api.dto.AppleTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AppleAuthClient", url = "https://appleid.apple.com")
public interface AppleOauthClient {

    @PostMapping(value = "/auth/token", consumes = "application/x-www-form-urlencoded")
    AppleTokenDto getAppleTokens(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("grant_type") String grantType, // "authorization_code" 또는 "refresh_token"
            @RequestParam("redirect_uri") String redirectUri);

    @GetMapping("/auth/keys")
    ApplePublicKeysDto getAppleOIDCOpenKeys();
}
