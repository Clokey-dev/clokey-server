package org.clokey.history.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_history_created", columnList = "history_id, created_at"),
                @Index(name = "idx_comment_member_id", columnList = "member_id")
        }
)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(nullable = false)
    private boolean banned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(String content, Member member, History history, Comment parent, boolean banned) {
        this.content = content;
        this.member = member;
        this.history = history;
        this.parent = parent;
        this.banned = banned;
    }

    public static Comment createComment(String content, Member member, History history, Comment parent) {
        return Comment.builder()
                .content(content)
                .member(member)
                .history(history)
                .parent(parent)
                .banned(false)
                .build();
    }

}