package org.clokey.domain.coordinate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.clokey.dto.ErrorReasonDto;
import org.clokey.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum CoordinateErrorCode implements BaseErrorCode {
    DAILY_COORDINATE_ALREADY_EXISTS(400, "COORDINATE_4001", "오늘의 코디가 이미 존재합니다");

    private final int status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getErrorReason() {
        return ErrorReasonDto.of(status, code, message);
    }
}
