package org.clokey.history.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String name;

    //    @Builder(access = AccessLevel.PRIVATE)
    //    private Hashtag(String name) {
    //        this.name = name;
    //    }
    //
    //    public static Hashtag createHashtag(String name) {
    //        return Hashtag.builder().name(name).build();
    //    }
}
