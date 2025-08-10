package org.clokey.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.clokey.comment.entitiy.Comment;
import org.clokey.comment.entitiy.Reply;
import org.clokey.domain.comment.dto.request.CommentCreateRequest;
import org.clokey.domain.comment.dto.request.ReplyCreateRequest;
import org.clokey.domain.comment.dto.response.CommentCreateResponse;
import org.clokey.domain.comment.dto.response.ReplyCreateResponse;
import org.clokey.domain.comment.exception.CommentErrorCode;
import org.clokey.domain.comment.repository.CommentRepository;
import org.clokey.domain.comment.repository.ReplyRepository;
import org.clokey.domain.history.exception.HistoryErrorCode;
import org.clokey.domain.history.repository.HistoryRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.FakeAuthContext;
import org.clokey.history.entity.History;
import org.clokey.member.entity.Member;
import org.clokey.member.enums.Visibility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final FakeAuthContext fakeAuthContext;

    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;
    private final ReplyRepository replyRepository;

    @Override
    public CommentCreateResponse createComment(CommentCreateRequest request) {
        final Member currentMember = fakeAuthContext.getCurrentMember();
        final History history = getHistoryById(request.historyId());

        validateHistoryAuthority(currentMember, history);

        Comment comment = Comment.createComment(request.content(), currentMember, history);
        commentRepository.save(comment);

        return CommentCreateResponse.from(comment);
    }

    @Override
    public ReplyCreateResponse createReply(Long commentId, ReplyCreateRequest request) {
        final Member currentMember = fakeAuthContext.getCurrentMember();
        final Comment comment = getCommentById(commentId);

        validateHistoryAuthority(currentMember, comment.getHistory());

        Reply reply = Reply.createReply(request.content(), currentMember, comment);
        replyRepository.save(reply);

        return ReplyCreateResponse.from(reply);
    }

    private void validateHistoryAuthority(Member member, History history) {
        if (history.getMember().getVisibility().equals(Visibility.PRIVATE)
                || !history.getMember().getId().equals(member.getId())) {
            throw new BaseCustomException(HistoryErrorCode.LIMITED_AUTHORITY);
        }
    }

    private History getHistoryById(Long historyId) {
        return historyRepository
                .findById(historyId)
                .orElseThrow(() -> new BaseCustomException(HistoryErrorCode.HISTORY_NOT_FOUND));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new BaseCustomException(CommentErrorCode.COMMENT_NOT_FOUND));
    }
}
