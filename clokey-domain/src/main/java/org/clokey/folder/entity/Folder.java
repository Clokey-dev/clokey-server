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

    @Column(nullable = false, length = 30)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    //    private List<ClothFolder> clothFolders = new ArrayList<>();
}
