package org.clokey.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "block")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private Member blocker; // 차단한 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked; // 차단된 회원

    @Builder(access = AccessLevel.PRIVATE)
    private Block(Member blocker, Member blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }

    public static Block createBlock(Member blocker, Member blocked) {
        return Block.builder().blocker(blocker).blocked(blocked).build();
    }
}
