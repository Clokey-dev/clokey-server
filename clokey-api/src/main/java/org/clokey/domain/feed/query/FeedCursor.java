package org.clokey.domain.feed.query;

import java.time.LocalDateTime;

public record FeedCursor(LocalDateTime createdAt, Long feedId) {}
