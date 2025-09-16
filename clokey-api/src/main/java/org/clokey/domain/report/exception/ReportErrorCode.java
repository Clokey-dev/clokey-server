package org.clokey.domain.report.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.clokey.dto.ErrorReasonDto;
import org.clokey.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {
    REPROT_DUPLICATED(400, "REPORT_4001", "신고는 한번만 가능합니다."),

    COMMENT_NOT_FOUND(404, "REPORT_4041", "존재하지 않는 댓글입니다."),
    REPLY_NOT_FOUND(404, "REPORT_4042", "존재하지 않는 대댓글입니다."),
    HISTORY_NOT_FOUND(404, "REPORT_4043", "존재하지 않는 기록입니다.");

    private final int status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getErrorReason() {
        return ErrorReasonDto.of(status, code, message);
    }
}
