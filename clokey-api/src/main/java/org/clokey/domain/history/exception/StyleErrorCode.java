package org.clokey.domain.history.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.clokey.dto.ErrorReasonDto;
import org.clokey.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum StyleErrorCode implements BaseErrorCode {
    INVALID_STYLE_COUNT(400, "STYLE_4001", "스타일은 1개 이상 3개 이하만 선택 가능합니다."),
    STYLE_NOT_FOUND(404, "STYLE_4041", "해당 스타일을 찾을 수 없습니다.");
    private final int status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getErrorReason() {
        return ErrorReasonDto.of(status, code, message);
    }
}
