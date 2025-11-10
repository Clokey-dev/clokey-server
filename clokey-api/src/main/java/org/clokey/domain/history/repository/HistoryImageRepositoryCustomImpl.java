package org.clokey.domain.history.repository;

import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.history.entity.HistoryImage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HistoryImageRepositoryCustomImpl implements HistoryImageRepositoryCustom {
    private static final int BATCH_SIZE = 100;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsertHistoryImages(List<HistoryImage> images) {
        if (images == null || images.isEmpty()) return;

        final String sql =
                "INSERT INTO history_image (history_id, image_url, created_at, updated_at) "
                        + "VALUES (?, ?, NOW(), NOW())";

        jdbcTemplate.batchUpdate(
                sql,
                images,
                BATCH_SIZE,
                (PreparedStatement ps, HistoryImage img) -> {
                    ps.setLong(1, img.getHistory().getId());
                    ps.setString(2, img.getImageUrl());
                });
    }
}
