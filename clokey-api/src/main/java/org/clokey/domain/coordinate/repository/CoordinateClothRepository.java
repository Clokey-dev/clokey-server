package org.clokey.domain.coordinate.repository;

import java.util.List;
import org.clokey.coordinate.entity.CoordinateCloth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CoordinateClothRepository extends JpaRepository<CoordinateCloth, Long> {

    @Modifying
    @Query("DELETE FROM CoordinateCloth cc WHERE cc.coordinate.id = :coordinateId")
    void deleteAllByCoordinateId(Long coordinateId);

    /** 특정 코디에 속하는 CoordinateCloth를 가져올 때 Cloth와 Category를 Fetch Join합니다. */
    @Query(
            """
        select cc
        from CoordinateCloth cc
        join fetch cc.cloth c
        join fetch c.category cat
        where cc.coordinate.id = :coordinateId
        order by cc.order asc
    """)
    List<CoordinateCloth> findAllCoordinateClothFetchClothByCoordinateId(Long coordinateId);
}
