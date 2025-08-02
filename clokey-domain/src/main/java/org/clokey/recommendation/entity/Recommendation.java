package org.clokey.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.member.entity.Member;
import org.clokey.recommendation.enums.NewsType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private Long contentId;

    @Column(length = 500)
    private String imageUrl;

    @Column private Double temperature;

    @Column(length = 1000)
    private String clothesIds;

    @Column(length = 500)
    private String hashtag;

    @Column(length = 500)
    private String subTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NewsType newsType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    private Recommendation(
            Long contentId,
            String imageUrl,
            Double temperature,
            String clothesIds,
            String hashtag,
            String subTitle,
            NewsType newsType,
            Member member) {
        this.contentId = contentId;
        this.imageUrl = imageUrl;
        this.temperature = temperature;
        this.clothesIds = clothesIds;
        this.hashtag = hashtag;
        this.subTitle = subTitle;
        this.newsType = newsType;
        this.member = member;
    }

    public static Recommendation createRecommendation(
            Long contentId,
            String imageUrl,
            Double temperature,
            String clothesIds,
            String hashtag,
            String subTitle,
            NewsType newsType,
            Member member) {
        return Recommendation.builder()
                .contentId(contentId)
                .imageUrl(imageUrl)
                .temperature(temperature)
                .clothesIds(clothesIds)
                .hashtag(hashtag)
                .subTitle(subTitle)
                .newsType(newsType)
                .member(member)
                .build();
    }
}
