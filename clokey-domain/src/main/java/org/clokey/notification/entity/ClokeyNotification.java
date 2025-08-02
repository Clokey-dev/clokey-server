package org.clokey.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.member.entity.Member;
import org.clokey.notification.enums.ReadStatus;
import org.clokey.notification.enums.RedirectType;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClokeyNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(nullable = false)
    private String notificationImageUrl;

    // ex) historyId, clokeyId
    @Column(nullable = false)
    private String redirectInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RedirectType redirectType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'NOT_READ'", nullable = false)
    private ReadStatus readStatus;

    @Builder(access = AccessLevel.PRIVATE)
    private ClokeyNotification(Member member,
                               String content,
                               String notificationImageUrl,
                               String redirectInfo,
                               RedirectType redirectType,
                               ReadStatus readStatus) {
        this.member = member;
        this.content = content;
        this.notificationImageUrl = notificationImageUrl;
        this.redirectInfo = redirectInfo;
        this.redirectType = redirectType;
        this.readStatus = readStatus;
    }

    public static ClokeyNotification createClokeyNotification(Member member,
                                            String content,
                                            String notificationImageUrl,
                                            String redirectInfo,
                                            RedirectType redirectType) {
        return ClokeyNotification.builder()
                .member(member)
                .content(content)
                .notificationImageUrl(notificationImageUrl)
                .redirectInfo(redirectInfo)
                .redirectType(redirectType)
                .readStatus(ReadStatus.NOT_READ)
                .build();
    }

}