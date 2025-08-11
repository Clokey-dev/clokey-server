package org.clokey.outer.api.dto;

public record OIDCDecodeDto(
        /** issuer ex https://kauth.kakao.com */
        String iss,
        /** client id */
        String aud,
        /** oauth provider account unique id */
        String sub,
        String email) {}
