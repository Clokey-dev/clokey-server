package org.clokey.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.clokey.dto.ErrorReasonDto;
import org.clokey.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    BANNED_MEMBER_TO_PUBLIC(404, "MEMBER_4013", "신고당한 회원은 공개로 전환할 수 없습니다."),
    ;

    private int status;
    private String code;
    private String message;

    @Override
    public ErrorReasonDto getErrorReason() {
        return org.clokey.dto.ErrorReasonDto.of(status, code, message);
    }
}
