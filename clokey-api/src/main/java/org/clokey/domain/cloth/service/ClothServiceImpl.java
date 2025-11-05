package org.clokey.domain.cloth.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.clokey.category.entity.Category;
import org.clokey.cloth.entity.Cloth;
import org.clokey.cloth.enums.Season;
import org.clokey.domain.category.exception.CategoryErrorCode;
import org.clokey.domain.category.repository.CategoryRepository;
import org.clokey.domain.cloth.dto.request.ClothCreateRequest;
import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothCreateResponse;
import org.clokey.domain.cloth.dto.response.ClothListResponse;
import org.clokey.domain.cloth.dto.response.ClothRecommendListResponse;
import org.clokey.domain.cloth.repository.ClothRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.paging.SortDirection;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.clokey.response.SliceResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClothServiceImpl implements ClothService {

    private final MemberUtil memberUtil;

    private final ClothRepository clothRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ClothCreateResponse createClothes(ClothCreateRequests request) {
        final Member currentMember = memberUtil.getCurrentMember();

        Map<Long, Category> categoryMap =
                getCategoryMapByIds(
                        request.content().stream()
                                .map(ClothCreateRequest::categoryId)
                                .collect(Collectors.toSet()));

        List<Cloth> clothes =
                request.content().stream()
                        .map(
                                cr -> {
                                    Category category = categoryMap.get(cr.categoryId());
                                    return Cloth.createCloth(
                                            cr.clothImageUrl(),
                                            category,
                                            cr.season(),
                                            currentMember);
                                })
                        .toList();

        clothRepository.saveAll(clothes);

        return ClothCreateResponse.from(clothes);
    }

    @Override
    public SliceResponse<ClothRecommendListResponse> recommendCategoryClothes(
            Long lastClothId, int size, Long categoryId, Season season) {
        final Member currentMember = memberUtil.getCurrentMember();

        Slice<ClothRecommendListResponse> result =
                clothRepository.findAllMemberRecommendClothesByCategoryAndSeason(
                        lastClothId, size, categoryId, currentMember.getId(), season);

        return SliceResponse.from(result);
    }

    @Override
    public SliceResponse<ClothListResponse> getClothes(
            Long lastClothId,
            int size,
            SortDirection direction,
            Long categoryId,
            List<Season> seasons) {
        final Member currentMember = memberUtil.getCurrentMember();

        List<Long> categoryIds = resolveCategoryIds(categoryId);

        Slice<ClothListResponse> result =
                clothRepository.findAllMemberClothesByCategoriesAndSeasons(
                        lastClothId, size, direction, categoryIds, currentMember.getId(), seasons);

        return SliceResponse.from(result);
    }

    private Map<Long, Category> getCategoryMapByIds(Set<Long> ids) {
        if (categoryRepository.countByIdIn(ids) != ids.size()) {
            throw new BaseCustomException(CategoryErrorCode.CATEGORY_IN_BULK_NOT_FOUND);
        }

        return categoryRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new BaseCustomException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 주어진 categoryId를 기반으로 조회용 카테고리 ID 목록을 생성합니다. - categoryId가 null이면 null을 반환하여 레포지토리에서 전체 조회되도록
     * 합니다. - 1차 카테고리(부모)가 주어지면 자식 카테고리들의 ID를 반환합니다. - 2차 카테고리(자식)가 주어지면 해당 ID만 반환합니다.
     */
    private List<Long> resolveCategoryIds(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        Category category = getCategoryById(categoryId);

        if (category.getParent() == null) {
            return categoryRepository.findAllByParentId(categoryId).stream()
                    .map(Category::getId)
                    .toList();
        }

        return List.of(categoryId);
    }
}
