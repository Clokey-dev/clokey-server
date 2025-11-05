package org.clokey.domain.cloth.repository;

import static org.clokey.cloth.entity.QCloth.cloth;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.cloth.enums.Season;
import org.clokey.domain.cloth.dto.response.ClothRecommendListResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ClothRepositoryImpl implements ClothRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ClothRecommendListResponse> findAllClothesByCategoryAndSeason(
            Long lastClothId, int size, Long categoryId, Season season) {

        Season nextSeason = season.next();
        Season previousSeason = season.previous();
        Season oppositeSeason = season.next().next();

        /** 우선 순위에 맞게 페이징 합니다. - Category는 고정입니다. - 계절은 요청한 계절에 가까운 순서대로 페이징을 진행합니다. */
        NumberExpression<Integer> seasonPriority =
                new CaseBuilder()
                        .when(cloth.season.eq(season))
                        .then(1)
                        .when(cloth.season.in(nextSeason, previousSeason))
                        .then(2)
                        .when(cloth.season.eq(oppositeSeason))
                        .then(3)
                        .otherwise(4);

        List<ClothRecommendListResponse> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ClothRecommendListResponse.class,
                                        cloth.id,
                                        cloth.clothImageUrl))
                        .from(cloth)
                        .where(
                                cloth.category.id.eq(categoryId),
                                cloth.season.in(season, nextSeason, previousSeason, oppositeSeason),
                                lastClothIdCondition(lastClothId))
                        .orderBy(seasonPriority.asc(), cloth.id.desc())
                        .limit((long) size + 1)
                        .fetch();

        return checkLastPage(size, results);
    }

    private BooleanExpression lastClothIdCondition(Long lastClothId) {
        if (lastClothId == null) {
            return null;
        }
        return cloth.id.lt(lastClothId);
    }

    private <T> Slice<T> checkLastPage(int pageSize, List<T> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}
