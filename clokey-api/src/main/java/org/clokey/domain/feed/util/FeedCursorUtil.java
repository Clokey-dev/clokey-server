package org.clokey.domain.feed.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.clokey.domain.feed.query.FeedCursor;
import org.clokey.exception.BaseCustomException;
import org.clokey.exception.GlobalBaseErrorCode;

public final class FeedCursorUtil {

    private static final String DELIMITER = "|";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private FeedCursorUtil() {}

    public static String encode(LocalDateTime createdAt, Long feedId) {
        if (createdAt == null || feedId == null) {
            return null;
        }
        String raw = createdAt.format(FORMATTER) + DELIMITER + feedId;
        return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static FeedCursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String decoded =
                    new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|");
            if (parts.length != 2) {
                throw new BaseCustomException(GlobalBaseErrorCode.BAD_REQUEST);
            }
            LocalDateTime createdAt = LocalDateTime.parse(parts[0], FORMATTER);
            Long feedId = Long.parseLong(parts[1]);
            return new FeedCursor(createdAt, feedId);
        } catch (IllegalArgumentException e) {
            throw new BaseCustomException(GlobalBaseErrorCode.BAD_REQUEST);
        }
    }
}
