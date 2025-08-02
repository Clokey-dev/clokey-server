package org.clokey.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"following_user_id", "followed_user_id"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_user_id", nullable = false)
    private Member following; // 팔로우 당하는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_user_id", nullable = false)
    private Member followed;  // 팔로우 하는 사람

    @Builder(access = AccessLevel.PRIVATE)
    private Follow(Member following, Member followed) {
        this.following = following;
        this.followed = followed;
    }

    public static Follow createFollow(Member following, Member followed) {
        return Follow.builder()
                .following(following)
                .followed(followed)
                .build();
    }
}
