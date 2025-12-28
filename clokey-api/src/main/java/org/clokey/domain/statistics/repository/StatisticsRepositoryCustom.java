package org.clokey.domain.statistics.repository;

import java.util.List;
import org.clokey.domain.statistics.dto.CategoryCountDto;

public interface StatisticsRepositoryCustom {
    List<CategoryCountDto> countClothesByChildCategories(Long memberId, Long parentCategoryId);
}
