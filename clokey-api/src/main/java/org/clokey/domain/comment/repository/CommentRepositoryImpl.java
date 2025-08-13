package org.clokey.domain.comment.repository;

import static org.clokey.comment.entitiy.QComment.comment;
import static org.clokey.member.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.comment.dto.response.CommentListResponse;
import org.clokey.global.paging.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<CommentListResponse> findAllByHistoryId(
            Long historyId, Long lastCommentId, int size, SortDirection direction) {
        List<CommentListResponse> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        CommentListResponse.class,
                                        comment.id,
                                        member.id,
                                        member.nickname,
                                        member.profileImageUrl,
                                        comment.content))
                        .from(comment)
                        .join(comment.member, member)
                        .where(
                                comment.member.id.eq(member.id),
                                lastCommentIdCondition(lastCommentId, direction))
                        .orderBy(
                                direction == SortDirection.DESC
                                        ? comment.id.desc()
                                        : comment.id.asc())
                        .limit(size + 1)
                        .fetch();

        return checkLastPage(size, results);
    }

    private BooleanExpression lastCommentIdCondition(Long commentId, SortDirection direction) {
        if (commentId == null) {
            return null;
        }

        return direction == SortDirection.DESC
                ? comment.id.lt(commentId)
                : comment.id.gt(commentId);
    }

    private <T> Slice<T> checkLastPage(int pageSize, List<T> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}
