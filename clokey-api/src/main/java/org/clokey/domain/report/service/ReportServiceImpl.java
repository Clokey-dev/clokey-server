package org.clokey.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.clokey.domain.comment.repository.CommentRepository;
import org.clokey.domain.comment.repository.ReplyRepository;
import org.clokey.domain.history.repository.HistoryRepository;
import org.clokey.domain.report.dto.request.ReportCreateRequest;
import org.clokey.domain.report.dto.response.ReportCreateResponse;
import org.clokey.domain.report.exception.ReportErrorCode;
import org.clokey.domain.report.repository.ReportRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.clokey.report.entity.Report;
import org.clokey.report.enums.TargetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final MemberUtil memberUtil;

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final HistoryRepository historyRepository;

    @Override
    @Transactional
    public ReportCreateResponse createReport(ReportCreateRequest request) {
        Member reporter = memberUtil.getCurrentMember();
        validateDuplicateReport(request, reporter);
        validateTargetExists(request.targetType(), request.targetId());

        Report report =
                Report.createReport(
                        request.targetId(),
                        reporter,
                        request.targetType(),
                        request.reportReason(),
                        request.content());

        reportRepository.save(report);

        return ReportCreateResponse.from(report);
    }

    private void validateDuplicateReport(ReportCreateRequest request, Member reporter) {
        boolean exists =
                reportRepository.existsByReporterAndTargetTypeAndTargetId(
                        reporter, request.targetType(), request.targetId());

        if (exists) {
            throw new BaseCustomException(ReportErrorCode.REPROT_DUPLICATED);
        }
    }

    private void validateTargetExists(TargetType targetType, Long targetId) {
        switch (targetType) {
            case COMMENT:
                if (!commentRepository.existsById(targetId)) {
                    throw new BaseCustomException(ReportErrorCode.COMMENT_NOT_FOUND);
                }
                break;
            case REPLY:
                if (!replyRepository.existsById(targetId)) {
                    throw new BaseCustomException(ReportErrorCode.REPLY_NOT_FOUND);
                }
                break;
            case HISTORY:
                if (!historyRepository.existsById(targetId)) {
                    throw new BaseCustomException(ReportErrorCode.HISTORY_NOT_FOUND);
                }
                break;
        }
    }
}
