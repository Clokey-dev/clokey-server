package org.clokey.domain.member.repository;

import org.clokey.member.entity.PendingFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingFollowRepository extends JpaRepository<PendingFollow, Long> {

    boolean existsByFollowFrom_IdAndFollowTo_Id(Long fromMemberId, Long toMemberId);

    void deleteByFollowFrom_IdAndFollowTo_Id(Long fromMemberId, Long toMemberId);
}
