package org.clokey.domain.history.repository;

import org.clokey.history.entity.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {
}
