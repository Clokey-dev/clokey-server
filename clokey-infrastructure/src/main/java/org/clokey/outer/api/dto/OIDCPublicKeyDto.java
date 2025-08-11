package org.clokey.outer.api.dto;

public record OIDCPublicKeyDto(String kid, String alg, String use, String n, String e) {}
