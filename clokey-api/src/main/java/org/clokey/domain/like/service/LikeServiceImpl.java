package org.clokey.domain.like.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.history.repository.HistoryImageRepository;
import org.clokey.domain.like.dto.response.LikedMembersResponse;
import org.clokey.domain.like.repository.MemberLikeRepository;
import org.clokey.domain.member.repository.FollowRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.entity.Member;
import org.clokey.response.SliceResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final MemberUtil memberUtil;
    private final MemberLikeRepository memberLikeRepository;
    private final HistoryImageRepository historyImageRepository;
    private final FollowRepository followRepository;

    @Override
    public SliceResponse<LikedMembersResponse.LikedMemberPreview> getLikedMembers(
            Long historyId, Long lastLikeId, Integer size) {

        Member currentMember = memberUtil.getCurrentMember();
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "id"));

        List<MemberLike> likes =
                memberLikeRepository.findLikeMembersByHistoryId(historyId, lastLikeId, pageable);

        boolean isLast = likes.size() <= size;

        if (!isLast) {
            likes = likes.subList(0, size);
        }

        if (likes.isEmpty()) {
            return new SliceResponse<>(List.of(), true);
        }

        List<Member> members = likes.stream().map(MemberLike::getMember).toList();
        List<Long> memberIds = members.stream().map(Member::getId).toList();

        Set<Long> followedIdSet =
                new HashSet<>(
                        followRepository.findFollowedMemberIds(currentMember.getId(), memberIds));

        List<LikedMembersResponse.LikedMemberPreview> previews =
                members.stream()
                        .map(
                                member ->
                                        new LikedMembersResponse.LikedMemberPreview(
                                                member.getId(),
                                                member.getClokeyId(),
                                                member.getProfileImageUrl(),
                                                member.getNickname(),
                                                followedIdSet.contains(member.getId())))
                        .toList();

        return new SliceResponse<>(previews, isLast);
    }
}
