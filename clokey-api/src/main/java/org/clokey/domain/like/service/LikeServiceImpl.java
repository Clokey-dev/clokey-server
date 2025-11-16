package org.clokey.domain.like.service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.history.repository.HistoryImageRepository;
import org.clokey.domain.like.dto.response.LikedHistoriesResponse;
import org.clokey.domain.like.repository.MemberLikeRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.history.entity.History;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.entity.Member;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final MemberUtil memberUtil;
    private final MemberLikeRepository memberLikeRepository;
    private final HistoryImageRepository historyImageRepository; // 이미지 조회용

    @Override
    @Transactional(readOnly = true)
    public LikedHistoriesResponse getLikedHistories(Pageable pageable) {

        final Member currentMember = memberUtil.getCurrentMember();

        // 좋아요 Slice 조회
        Slice<MemberLike> likes =
                memberLikeRepository.findLikedHistoriesByMemberId(currentMember.getId(), pageable);

        // 모든 historyId 모아서 한 번에 이미지 URL 조회 (N+1 방지)
        List<Long> historyIds =
                likes.getContent().stream().map(like -> like.getHistory().getId()).toList();

        Map<Long, String> historyImageMap = findFirstImagesByHistoryIds(historyIds);

        // DTO 변환
        List<LikedHistoriesResponse.LikedHistoryPreview> previews =
                likes.getContent().stream()
                        .map(
                                like -> {
                                    History history = like.getHistory();
                                    String firstImageUrl = historyImageMap.get(history.getId());
                                    return new LikedHistoriesResponse.LikedHistoryPreview(
                                            history.getId(), firstImageUrl);
                                })
                        .toList();

        // 결과 DTO 생성
        return new LikedHistoriesResponse(
                previews,
                likes.getNumber() + 1,
                likes.getNumberOfElements(),
                likes.isFirst(),
                likes.isLast());
    }

    private Map<Long, String> findFirstImagesByHistoryIds(List<Long> historyIds) {
        if (historyIds.isEmpty()) {
            return Map.of();
        }

        // DB에서 [historyId, firstImageUrl] 형태로 조회
        List<Object[]> rows = historyImageRepository.getFirstImageUrlsWithHistoryId(historyIds);

        return rows.stream()
                .collect(
                        Collectors.toMap(
                                row -> ((Number) row[0]).longValue(), row -> (String) row[1]));
    }
}
