package org.clokey.domain.member.repository;

import org.clokey.member.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowFrom_IdAndFollowTo_Id(Long followerId, Long followingId);

    void deleteByFollowFrom_IdAndFollowTo_Id(Long followerId, Long followingId);
}
