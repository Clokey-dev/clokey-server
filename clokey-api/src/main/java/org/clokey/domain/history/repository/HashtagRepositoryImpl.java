package org.clokey.domain.history.repository;

import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.clokey.history.entity.Hashtag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HashtagRepositoryImpl implements HashtagRepositoryCustom {
    private static final int BATCH_SIZE = 100;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Hashtag> bulkInsertHashtags(List<Hashtag> links) {
        if (links == null || links.isEmpty()) return links;

        final String sql =
                "INSERT INTO hashtag (name, created_at, updated_at) " + "VALUES (?, NOW(), NOW())";

        jdbcTemplate.batchUpdate(
                sql,
                links,
                BATCH_SIZE,
                (PreparedStatement ps, Hashtag h) -> {
                    ps.setString(1, h.getName());
                });

        return links;
    }
}
