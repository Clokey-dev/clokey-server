package org.clokey.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "follow_request",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_follow_request_to_id_from_id",
                    columnNames = {"follow_to_id", "follow_from_id"})
        })
public class PendingFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_to_id")
    @NotNull
    private Member pendingFollowTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_from_id")
    @NotNull
    private Member pendingFollowFrom;

    @Builder(access = AccessLevel.PRIVATE)
    public PendingFollow(Member pendingFollowTo, Member pendingFollowFrom) {
        this.pendingFollowTo = pendingFollowTo;
        this.pendingFollowFrom = pendingFollowFrom;
    }

    public static PendingFollow createPendingFollow(
            Member pendingFollowFrom, Member pendingFollowTo) {
        return PendingFollow.builder()
                .pendingFollowFrom(pendingFollowFrom)
                .pendingFollowTo(pendingFollowTo)
                .build();
    }
}
