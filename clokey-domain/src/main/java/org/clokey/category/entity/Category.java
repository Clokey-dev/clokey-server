package org.clokey.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    //    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    //    private List<Category> children = new ArrayList<>();
}
