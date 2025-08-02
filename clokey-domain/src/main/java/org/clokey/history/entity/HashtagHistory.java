package org.clokey.history.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clokey.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
            @Index(name = "idx_history_id", columnList = "history_id"),
            @Index(name = "idx_hashtag_id", columnList = "hashtag_id")
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_history_hashtag",
                    columnNames = {"history_id", "hashtag_id"})
        })
public class HashtagHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Builder(access = AccessLevel.PRIVATE)
    private HashtagHistory(Hashtag hashtag, History history) {
        this.hashtag = hashtag;
        this.history = history;
    }

    public static HashtagHistory createHashtagHistory(Hashtag hashtag, History history) {
        return HashtagHistory.builder().hashtag(hashtag).history(history).build();
    }
}
