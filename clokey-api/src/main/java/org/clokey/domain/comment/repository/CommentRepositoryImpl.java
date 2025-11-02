package org.clokey.domain.comment.repository;

import static org.clokey.comment.entitiy.QComment.comment1;
import static org.clokey.member.entity.QMember.member;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.clokey.comment.entitiy.QComment;
import org.clokey.domain.comment.dto.response.CommentListResponse;
import org.clokey.domain.comment.dto.response.ReplyListResponse;
import org.clokey.global.paging.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QComment comment = comment1;

    @Override
    public Slice<CommentListResponse> findAllParentCommentByHistoryId(
            Long historyId,
            Long currentMemberId,
            Long lastCommentId,
            int size,
            SortDirection direction) {

        // 부모 댓글만 조회
        List<CommentListResponse> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        CommentListResponse.class,
                                        comment.id,
                                        member.id,
                                        member.nickname,
                                        member.profileImageUrl,
                                        comment.content,
                                        Expressions.constant(false),
                                        member.id.eq(currentMemberId)))
                        .from(comment)
                        .join(comment.member, member)
                        .where(
                                comment.history.id.eq(historyId),
                                comment.comment.isNull(),
                                lastCommentIdCondition(lastCommentId, direction))
                        .orderBy(
                                direction == SortDirection.DESC
                                        ? comment.id.desc()
                                        : comment.id.asc())
                        .limit(size + 1)
                        .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        // 각 부모 댓글이 대댓글을 가지고 있는지 여부 조회
        Map<Long, Boolean> repliedMap =
                results.isEmpty()
                        ? Map.of()
                        : queryFactory
                                .select(comment.comment.id)
                                .from(comment)
                                .where(
                                        comment.comment.id.in(
                                                results.stream()
                                                        .map(CommentListResponse::commentId)
                                                        .toList()))
                                .groupBy(comment.comment.id)
                                .transform(
                                        GroupBy.groupBy(comment.comment.id)
                                                .as(Expressions.constant(true)));

        List<CommentListResponse> finalResults =
                results.stream()
                        .map(
                                c ->
                                        new CommentListResponse(
                                                c.commentId(),
                                                c.memberId(),
                                                c.nickName(),
                                                c.profileImageUrl(),
                                                c.content(),
                                                repliedMap.getOrDefault(c.commentId(), false),
                                                c.isMine()))
                        .toList();

        return new SliceImpl<>(finalResults, PageRequest.of(0, size), hasNext);
    }

    @Override
    public Slice<ReplyListResponse> findAllRepliesByCommentId(
            Long commentId,
            Long currentMemberId,
            Long lastReplyId,
            int size,
            SortDirection direction) {

        List<ReplyListResponse> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ReplyListResponse.class,
                                        comment.id,
                                        member.id,
                                        member.nickname,
                                        member.profileImageUrl,
                                        comment.content,
                                        member.id.eq(currentMemberId)))
                        .from(comment)
                        .join(comment.member, member)
                        .where(
                                comment.comment.id.eq(commentId),
                                lastCommentIdCondition(lastReplyId, direction))
                        .orderBy(
                                direction == SortDirection.DESC
                                        ? comment.id.desc()
                                        : comment.id.asc())
                        .limit(size + 1)
                        .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, PageRequest.of(0, size), hasNext);
    }

    private BooleanExpression lastCommentIdCondition(Long commentId, SortDirection direction) {
        if (commentId == null) {
            return null;
        }

        return direction == SortDirection.DESC
                ? comment.id.lt(commentId)
                : comment.id.gt(commentId);
    }
}
