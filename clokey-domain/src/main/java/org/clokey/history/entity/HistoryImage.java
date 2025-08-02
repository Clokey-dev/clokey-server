package org.clokey.history.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "history_image",
        indexes = {@Index(name = "idx_history_created_at", columnList = "history_id, created_at")})
public class HistoryImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Builder(access = AccessLevel.PRIVATE)
    private HistoryImage(String imageUrl, History history) {
        this.imageUrl = imageUrl;
        this.history = history;
    }

    public static HistoryImage createHistoryImage(String imageUrl, History history) {
        return HistoryImage.builder().imageUrl(imageUrl).history(history).build();
    }
}
