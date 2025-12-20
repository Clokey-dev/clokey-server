package org.clokey.domain.like.repository;

import java.util.List;
import org.clokey.like.entity.MemberLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    @Query(
            """
        SELECT ml
        FROM MemberLike ml
        WHERE ml.member.id = :memberId
          AND (:lastLikeId IS NULL OR ml.id < :lastLikeId)
        ORDER BY ml.id DESC
        """)
    List<MemberLike> findLikedHistoriesByMemberId(
            Long memberId, Long lastLikeId, Pageable pageable);
}
