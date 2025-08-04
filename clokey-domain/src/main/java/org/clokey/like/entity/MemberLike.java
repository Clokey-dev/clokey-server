package org.clokey.like.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.history.entity.History;
import org.clokey.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    //    @Builder(access = AccessLevel.PRIVATE)
    //    private MemberLike(Member member, History history) {
    //        this.member = member;
    //        this.history = history;
    //    }
    //
    //    public static MemberLike createMemberLike(Member member, History history) {
    //        return MemberLike.builder().member(member).history(history).build();
    //    }
}
