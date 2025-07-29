package org.clokey.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.clokey.dto.ErrorReasonDto;

@Getter
@AllArgsConstructor
public enum GlobalBaseErrorCode implements BaseErrorCode {

    // 기본 에러
    _INTERNAL_SERVER_ERROR(500, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(400, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(401, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(403, "COMMON403", "금지된 요청입니다.");

    private final int status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getErrorReason() {
        return ErrorReasonDto.builder().status(status).code(code).message(message).build();
    }
}
