package org.clokey.domain.member.repository;

import org.clokey.member.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowFromIdAndFollowToId(Long followerId, Long followingId);

    void deleteByFollowFromIdAndFollowToId(Long followerId, Long followingId);
}
