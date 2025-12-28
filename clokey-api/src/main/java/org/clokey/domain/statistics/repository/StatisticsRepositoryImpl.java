package org.clokey.domain.statistics.repository;

import static org.clokey.cloth.entity.QCloth.cloth;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.statistics.dto.CategoryCountDto;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryImpl implements StatisticsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryCountDto> countClothesByChildCategories(
            Long memberId, Long parentCategoryId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryCountDto.class,
                                cloth.category.id,
                                cloth.category.name,
                                cloth.id.count()))
                .from(cloth)
                .where(cloth.member.id.eq(memberId), cloth.category.parent.id.eq(parentCategoryId))
                .groupBy(cloth.category)
                .orderBy(cloth.id.count().desc())
                .fetch();
    }

    @Override
    public List<CategoryCountDto> countClothesByCategoriesTopN(Long memberId, int limit) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryCountDto.class,
                                cloth.category.id,
                                cloth.category.name,
                                cloth.id.count()))
                .from(cloth)
                .where(cloth.member.id.eq(memberId))
                .groupBy(cloth.category)
                .orderBy(cloth.id.count().desc())
                .limit(limit)
                .fetch();
    }
}
