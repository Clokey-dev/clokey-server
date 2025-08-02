package org.clokey.domain.cloth.repository;

import org.clokey.cloth.entity.ClothImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long>, ClothProjectionRepository {
}
