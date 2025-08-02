package org.clokey.folder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;
import org.clokey.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Long itemCount = 0L;

    @Builder(access = AccessLevel.PRIVATE)
    private Folder(Member member, String name, Long itemCount) {
        this.member = member;
        this.name = name;
        this.itemCount = itemCount != null ? itemCount : 0L;
    }

    public static Folder createFolder(Member member, String name, Long itemCount) {
        return Folder.builder()
                .member(member)
                .name(name)
                .itemCount(itemCount)
                .build();
    }

    public static Folder createFolder(Member member, String name) {
        return createFolder(member, name, 0L);
    }

}
