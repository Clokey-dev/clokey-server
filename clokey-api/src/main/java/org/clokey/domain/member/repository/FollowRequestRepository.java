package org.clokey.domain.member.repository;

import org.clokey.member.entity.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    boolean existsByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

    void deleteByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);
}
