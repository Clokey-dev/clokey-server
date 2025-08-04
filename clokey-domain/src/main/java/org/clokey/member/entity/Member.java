package org.clokey.member.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.clokey.cloth.entity.Cloth;
import org.clokey.comment.entitiy.Comment;
import org.clokey.comment.entitiy.Reply;
import org.clokey.common.model.BaseEntity;
import org.clokey.history.entity.History;
import org.clokey.like.entity.MemberLike;
import org.clokey.member.enums.MemberStatus;
import org.clokey.member.enums.RegisterStatus;
import org.clokey.member.enums.SocialType;
import org.clokey.member.enums.Visibility;
import org.clokey.term.entity.MemberTerm;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
@Table(indexes = {@Index(name = "idx_member_clokey_id", columnList = "clokeyId")})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(unique = true)
    private String clokeyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType; // 이넘으로 나중에 관리하기

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'", nullable = false)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(30) DEFAULT 'NOT_AGREED'", nullable = false)
    private RegisterStatus registerStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'PUBLIC'", nullable = false)
    private Visibility visibility;

    private String profileImageUrl;
    private String profileBackImageUrl;
    private String bio;
    private String refreshToken;
    private String accessToken;
    private String deviceToken;
    private String appleRefreshToken;
    private String kakaoId;

    @Column(nullable = false)
    private boolean banned = false;

    private LocalDate inactiveDate;

    // 연관 관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTerm> memberTermList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberLike> memberLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Cloth> clothList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<History> historyList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Member(
            String email,
            String nickname,
            String clokeyId,
            SocialType socialType,
            MemberStatus status,
            RegisterStatus registerStatus,
            Visibility visibility,
            String profileImageUrl,
            String profileBackImageUrl,
            String bio,
            String refreshToken,
            String accessToken,
            String deviceToken,
            String appleRefreshToken,
            String kakaoId,
            boolean banned,
            LocalDate inactiveDate) {
        this.email = email;
        this.nickname = nickname;
        this.clokeyId = clokeyId;
        this.socialType = socialType;
        this.status = status;
        this.registerStatus = registerStatus;
        this.visibility = visibility;
        this.profileImageUrl = profileImageUrl;
        this.profileBackImageUrl = profileBackImageUrl;
        this.bio = bio;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.deviceToken = deviceToken;
        this.appleRefreshToken = appleRefreshToken;
        this.kakaoId = kakaoId;
        this.banned = banned;
        this.inactiveDate = inactiveDate;
    }

    // ✅ 정적 팩토리 메서드
    public static Member createMember(
            String email,
            String nickname,
            String clokeyId,
            SocialType socialType,
            MemberStatus status,
            RegisterStatus registerStatus,
            Visibility visibility,
            String profileImageUrl,
            String profileBackImageUrl,
            String bio,
            String refreshToken,
            String accessToken,
            String deviceToken,
            String appleRefreshToken,
            String kakaoId,
            boolean banned,
            LocalDate inactiveDate) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .clokeyId(clokeyId)
                .socialType(socialType)
                .status(status)
                .registerStatus(registerStatus)
                .visibility(visibility)
                .profileImageUrl(profileImageUrl)
                .profileBackImageUrl(profileBackImageUrl)
                .bio(bio)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .deviceToken(deviceToken)
                .appleRefreshToken(appleRefreshToken)
                .kakaoId(kakaoId)
                .banned(banned)
                .inactiveDate(inactiveDate)
                .build();
    }
}
