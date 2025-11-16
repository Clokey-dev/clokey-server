package org.clokey.domain.like.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.history.repository.HistoryImageRepository;
import org.clokey.domain.like.dto.response.LikedHistoriesResponse;
import org.clokey.domain.like.repository.MemberLikeRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.entity.Member;
import org.clokey.response.SliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final MemberUtil memberUtil;
    private final MemberLikeRepository memberLikeRepository;
    private final HistoryImageRepository historyImageRepository;

    @Override
    @Transactional(readOnly = true)
    public SliceResponse<LikedHistoriesResponse.LikedHistoryPreview> getLikedHistories(
            Pageable pageable) {

        final Member currentMember = memberUtil.getCurrentMember();

        Slice<MemberLike> likes =
                memberLikeRepository.findLikedHistoriesByMemberId(currentMember.getId(), pageable);

        if (likes == null || likes.getContent().isEmpty()) {
            return new SliceResponse<>(List.of(), true);
        }

        List<Long> historyIds =
                likes.getContent().stream().map(like -> like.getHistory().getId()).toList();

        Map<Long, String> imageMap = findFirstImagesByHistoryIds(historyIds);

        List<LikedHistoriesResponse.LikedHistoryPreview> previews =
                likes.getContent().stream()
                        .map(
                                like ->
                                        new LikedHistoriesResponse.LikedHistoryPreview(
                                                like.getHistory().getId(),
                                                imageMap.get(like.getHistory().getId())))
                        .toList();

        return new SliceResponse<>(previews, likes.isLast());
    }

    private Map<Long, String> findFirstImagesByHistoryIds(List<Long> historyIds) {
        if (historyIds.isEmpty()) return Map.of();

        List<Object[]> rows = historyImageRepository.getFirstImageUrlsWithHistoryId(historyIds);

        return rows.stream()
                .collect(
                        Collectors.toMap(
                                row -> ((Number) row[0]).longValue(), row -> (String) row[1]));
    }
}
