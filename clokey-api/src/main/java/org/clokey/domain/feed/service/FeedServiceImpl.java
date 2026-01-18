package org.clokey.domain.feed.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.feed.dto.response.FeedListResponse;
import org.clokey.domain.feed.query.FeedCursor;
import org.clokey.domain.feed.query.FollowScope;
import org.clokey.domain.feed.util.FeedCursorUtil;
import org.clokey.domain.feed.util.FeedRequestParser;
import org.clokey.domain.history.repository.HistoryImageRepository;
import org.clokey.domain.feed.repository.FeedQueryRepository;
import org.clokey.domain.like.repository.MemberLikeRepository;
import org.clokey.domain.member.repository.FollowRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.history.entity.History;
import org.clokey.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedServiceImpl implements FeedService {

    private final MemberUtil memberUtil;
    private final FeedQueryRepository feedQueryRepository;
    private final HistoryImageRepository historyImageRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final FollowRepository followRepository;

    @Override
    public FeedListResponse getFeeds(
            FollowScope followScope,
            List<Long> styleIds,
            List<Long> situationIds,
            Integer size,
            String cursor) {
        final Member currentMember = memberUtil.getCurrentMember();
        final int pageSize = FeedRequestParser.parseSize(size, 10, 50);
        final FeedCursor decodedCursor = FeedCursorUtil.decode(cursor);

        // NOTE: Non-existent styleId/situationId values are not validated and are ignored by IN filters.
        List<History> histories =
                feedQueryRepository.findFeeds(
                        currentMember.getId(),
                        followScope == null ? FollowScope.ALL : followScope,
                        styleIds == null ? List.of() : styleIds,
                        situationIds == null ? List.of() : situationIds,
                        decodedCursor,
                        pageSize);

        boolean hasNext = histories.size() > pageSize;
        if (hasNext) {
            histories = histories.subList(0, pageSize);
        }

        if (histories.isEmpty()) {
            return FeedListResponse.of(List.of(), null, false);
        }

        List<Long> feedIds = histories.stream().map(History::getId).toList();
        List<Long> authorIds =
                histories.stream()
                        .map(h -> h.getMember().getId())
                        .distinct()
                        .toList();

        Map<Long, String> imageUrlMap = getImageUrls(feedIds);
        Set<Long> likedHistoryIds = getLikedHistoryIds(currentMember.getId(), feedIds);
        Set<Long> followedMemberIds = getFollowedMemberIds(currentMember.getId(), authorIds);
        List<FeedListResponse.FeedItemResponse> items =
                histories.stream()
                        .map(
                                history ->
                                        new FeedListResponse.FeedItemResponse(
                                                history.getId(),
                                                history.getCreatedAt(),
                                                imageUrlMap.get(history.getId()),
                                                likedHistoryIds.contains(history.getId()),
                                                toAuthorResponse(
                                                        history.getMember(),
                                                        followedMemberIds.contains(
                                                                history.getMember().getId()))))
                        .toList();

        History last = histories.get(histories.size() - 1);
        String nextCursorValue =
                hasNext ? FeedCursorUtil.encode(last.getCreatedAt(), last.getId()) : null;

        return FeedListResponse.of(items, nextCursorValue, hasNext);
    }

    private FeedListResponse.FeedAuthorResponse toAuthorResponse(
            Member member, boolean isFollowing) {
        return new FeedListResponse.FeedAuthorResponse(
                member.getId(), member.getClokeyId(), member.getProfileImageUrl(), isFollowing);
    }

    private Map<Long, String> getImageUrls(List<Long> feedIds) {
        if (feedIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> rows = historyImageRepository.getFirstImageUrlsWithHistoryId(feedIds);
        Map<Long, String> map = new HashMap<>();
        for (Object[] row : rows) {
            Long historyId = (Long) row[0];
            String imageUrl = (String) row[1];
            map.put(historyId, imageUrl);
        }
        return map;
    }

    private Set<Long> getLikedHistoryIds(Long memberId, List<Long> feedIds) {
        if (feedIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(memberLikeRepository.findLikedHistoryIds(memberId, feedIds));
    }

    private Set<Long> getFollowedMemberIds(Long memberId, List<Long> authorIds) {
        if (authorIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(followRepository.findFollowedMemberIds(memberId, authorIds));
    }
}
