package org.clokey.comment.entitiy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.history.entity.History;
import org.clokey.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull private boolean banned;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "history_id")
    private History history;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    //    @Builder(access = AccessLevel.PRIVATE)
    //    private Comment(
    //            String content, Member member, History history, boolean banned) {
    //        this.content = content;
    //        this.member = member;
    //        this.history = history;
    //        this.banned = banned;
    //    }
    //
    //    public static Comment createComment(
    //            String content, Member member, History history) {
    //        return Comment.builder()
    //                .content(content)
    //                .member(member)
    //                .history(history)
    //                .banned(false)
    //                .build();
    //    }
}
