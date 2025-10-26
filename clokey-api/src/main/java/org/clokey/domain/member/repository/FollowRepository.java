package org.clokey.domain.member.repository;

import org.clokey.member.entity.Follow;
import org.clokey.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

    boolean existsByFollowFromAndFollowTo(Member currentMember, Member targetMember);
}
