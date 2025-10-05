package org.clokey.domain.member.repository;

import static org.clokey.member.entity.QBlock.block;
import static org.clokey.member.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.member.dto.response.BlockedMemberResponse;
import org.clokey.global.paging.SortDirection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<BlockedMemberResponse> findAllByBlockerId(
            Long blockerId, Pageable pageable, SortDirection sortDirection) {
        List<BlockedMemberResponse> content =
                queryFactory
                        .select(
                                Projections.constructor(
                                        BlockedMemberResponse.class,
                                        member.id,
                                        member.clokeyId,
                                        member.profileImageUrl))
                        .from(block)
                        .join(block.blocked, member)
                        .where(block.blocker.id.eq(blockerId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .orderBy(
                                sortDirection == SortDirection.DESC
                                        ? block.createdAt.desc()
                                        : block.createdAt.asc())
                        .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
