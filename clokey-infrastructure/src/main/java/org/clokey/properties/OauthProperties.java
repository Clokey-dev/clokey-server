package org.clokey.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oidc")
public record OauthProperties(Kakao kakao, Apple apple) {
    public record Kakao(String clientId, String clientSecret, String appId, String adminKey) {}

    public record Apple(
            String teamId, String keyId, String clientId, String privateKey, String issuer) {}
}
