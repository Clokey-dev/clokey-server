package org.clokey.history.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.clokey.cloth.entity.Cloth;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "history_cloth",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_history_cloth_history_id_cloth_id",
                    columnNames = {"history_id", "cloth_id"})
        })
public class HistoryCloth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id", nullable = false)
    private Cloth cloth;

    @OneToMany(mappedBy = "historyCloth", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoryClothTag> clothTags = new ArrayList<>();

    //    @Builder(access = AccessLevel.PRIVATE)
    //    private HistoryCloth(History history, Cloth cloth) {
    //        this.history = history;
    //        this.cloth = cloth;
    //    }
    //
    //    public static HistoryCloth createHistoryCloth(History history, Cloth cloth) {
    //        return HistoryCloth.builder().history(history).cloth(cloth).build();
    //    }
}
