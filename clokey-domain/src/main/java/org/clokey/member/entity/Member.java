package org.clokey.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private String email;

    @Column(length = 30)
    private String nickname;

    @Column(unique = true)
    private String clokeyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RegisterStatus registerStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Visibility visibility;

    private String profileImageUrl;
    private String profileBackImageUrl;
    private String bio;
    private String deviceToken;
    private String oid;

    @NotNull private boolean banned;

    private LocalDate inactiveDate;

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
}
