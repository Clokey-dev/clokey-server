package org.clokey.domain.member.repository;

import org.clokey.domain.member.dto.response.BlockedMemberResponse;
import org.clokey.global.paging.SortDirection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BlockRepositoryCustom {

    Slice<BlockedMemberResponse> findAllByBlockerId(
            Long BlockerId, Pageable pageable, SortDirection sortDirection);
}
