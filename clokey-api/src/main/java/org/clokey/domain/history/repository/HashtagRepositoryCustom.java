package org.clokey.domain.history.repository;

import org.clokey.history.entity.Hashtag;

import java.util.List;

public interface HashtagRepositoryCustom {
    List<Hashtag> bulkInsertHashtags(List<Hashtag> links);
}
