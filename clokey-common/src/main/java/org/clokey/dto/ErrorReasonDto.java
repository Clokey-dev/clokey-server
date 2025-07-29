package org.clokey.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorReasonDto {
    private final int status;
    private final String code;
    private final String message;
}
