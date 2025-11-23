package org.clokey.domain.folder.repository;

import java.util.List;
import org.clokey.folder.entity.ClothFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothFolderRepository extends JpaRepository<ClothFolder, Long> {

    List<ClothFolder> findAllByClothId(Long clothId);
}
