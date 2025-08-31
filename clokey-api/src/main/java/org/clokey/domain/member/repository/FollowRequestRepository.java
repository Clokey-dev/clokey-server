package org.clokey.domain.member.repository;

import org.clokey.member.entity.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    boolean existsByFollowFrom_IdAndFollowTo_Id(Long fromMemberId, Long toMemberId);

    void deleteByFollowFrom_IdAndFollowTo_Id(Long fromMemberId, Long toMemberId);
}
