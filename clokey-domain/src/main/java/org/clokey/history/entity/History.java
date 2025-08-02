package org.clokey.history.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "history",
        indexes = {@Index(name = "idx_member_date", columnList = "member_id, history_date")})
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate historyDate;

    @Min(0)
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likes;

    @Column(length = 200)
    private String content;

    @Column(nullable = false)
    private boolean banned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    private History(
            LocalDate historyDate, int likes, String content, boolean banned, Member member) {
        this.historyDate = historyDate;
        this.likes = likes;
        this.content = content;
        this.banned = banned;
        this.member = member;
    }

    public static History createHistory(LocalDate historyDate, String content, Member member) {
        return History.builder()
                .historyDate(historyDate)
                .likes(0)
                .content(content)
                .banned(false)
                .member(member)
                .build();
    }
}
