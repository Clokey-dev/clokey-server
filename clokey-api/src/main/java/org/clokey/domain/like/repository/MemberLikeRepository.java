package org.clokey.domain.like.repository;

import java.awt.print.Pageable;
import org.clokey.like.entity.MemberLike;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    @Query("SELECT ml FROM MemberLike ml WHERE ml.member.id = :memberId")
    Slice<MemberLike> findLikedHistoriesByMemberId(Long memberId, Pageable pageable);
}
