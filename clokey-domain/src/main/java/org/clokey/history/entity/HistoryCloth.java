package org.clokey.history.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.cloth.entity.Cloth;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_history_cloth_history_id", columnList = "history_id")
        }
)
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

    @Builder(access = AccessLevel.PRIVATE)
    private HistoryCloth(History history, Cloth cloth) {
        this.history = history;
        this.cloth = cloth;
    }

    public static HistoryCloth createHistoryCloth(History history, Cloth cloth) {
        return HistoryCloth.builder()
                .history(history)
                .cloth(cloth)
                .build();
    }
}