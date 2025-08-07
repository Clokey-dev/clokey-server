package org.clokey.domain.category.repository;

import java.util.List;
import org.clokey.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(
            value =
                    """
            SELECT *
            FROM category
            WHERE id IN (:ids)
            ORDER BY FIELD(id, :#{#ids})
            """,
            nativeQuery = true)
    List<Category> findAllByIdInOrder(@Param("ids") List<Long> ids);
}
