package org.clokey.cloth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {@Index(name = "idx_cloth_image_cloth_id", columnList = "cloth_id")})
public class ClothImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 옷 이미지 URL

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id", nullable = false)
    private Cloth cloth;

    @Builder(access = AccessLevel.PRIVATE)
    private ClothImage(String imageUrl, Cloth cloth) {
        this.imageUrl = imageUrl;
        this.cloth = cloth;
    }

    public static ClothImage createClothImage(String imageUrl, Cloth cloth) {
        return ClothImage.builder().imageUrl(imageUrl).cloth(cloth).build();
    }
}
