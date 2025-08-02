package org.clokey.cloth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.clokey.category.entity.Category;
import org.clokey.cloth.enums.Season;
import org.clokey.cloth.enums.ThicknessLevel;
import org.clokey.common.model.BaseEntity;
import org.clokey.member.entity.Member;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Cloth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int wearNum;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cloth_season", joinColumns = @JoinColumn(name = "cloth_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false)
    private List<Season> seasons = new ArrayList<>();

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempUpperBound;

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempLowerBound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ThicknessLevel thicknessLevel;

    @Column(nullable = true, length = 1000)
    private String clothUrl;

    @Column(nullable = true)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(mappedBy = "cloth", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClothImage image;

    @Builder(access = AccessLevel.PRIVATE)
    private Cloth(
            String name,
            List<Season> seasons,
            int tempUpperBound,
            int tempLowerBound,
            ThicknessLevel thicknessLevel,
            String clothUrl,
            String brand,
            Category category,
            Member member) {
        this.name = name;
        this.seasons = seasons != null ? seasons : new ArrayList<>();
        this.tempUpperBound = tempUpperBound;
        this.tempLowerBound = tempLowerBound;
        this.thicknessLevel = thicknessLevel;
        this.clothUrl = clothUrl;
        this.brand = brand;
        this.category = category;
        this.member = member;
        this.wearNum = 0;
    }

    public static Cloth createCloth(
            String name,
            List<Season> seasons,
            int tempUpperBound,
            int tempLowerBound,
            ThicknessLevel thicknessLevel,
            String clothUrl,
            String brand,
            Category category,
            Member member) {
        return Cloth.builder()
                .name(name)
                .seasons(seasons)
                .tempUpperBound(tempUpperBound)
                .tempLowerBound(tempLowerBound)
                .thicknessLevel(thicknessLevel)
                .clothUrl(clothUrl)
                .brand(brand)
                .category(category)
                .member(member)
                .build();
    }
}
